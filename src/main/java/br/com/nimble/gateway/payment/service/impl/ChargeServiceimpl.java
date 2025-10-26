package br.com.nimble.gateway.payment.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.nimble.gateway.payment.api.v1.dto.request.CardPaymentRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.ChargeRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.ChargeResponse;
import br.com.nimble.gateway.payment.api.v1.mapper.ChargeMapper;
import br.com.nimble.gateway.payment.config.security.context.AuthenticatedUserProvider;
import br.com.nimble.gateway.payment.domain.exception.ObjectNotFoundException;
import br.com.nimble.gateway.payment.domain.exception.ValidationException;
import br.com.nimble.gateway.payment.domain.model.Charge;
import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.model.enums.ChargeStatus;
import br.com.nimble.gateway.payment.domain.model.enums.PaymentMethod;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionStatus;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionType;
import br.com.nimble.gateway.payment.domain.repository.ChargeRepository;
import br.com.nimble.gateway.payment.domain.strategy.CancellationAction;
import br.com.nimble.gateway.payment.domain.strategy.PaymentAction;
import br.com.nimble.gateway.payment.integration.AuthorizerAdapter;
import br.com.nimble.gateway.payment.service.AccountChargeService;
import br.com.nimble.gateway.payment.service.AccountService;
import br.com.nimble.gateway.payment.service.CardPaymentService;
import br.com.nimble.gateway.payment.service.ChargeService;
import br.com.nimble.gateway.payment.service.TransactionService;
import br.com.nimble.gateway.payment.service.UserChargeService;
import br.com.nimble.gateway.payment.util.LoginUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class ChargeServiceimpl implements ChargeService {

    private final ChargeRepository chargeRepository;
    private final AuthenticatedUserProvider authentication;
    private final UserChargeService userService;
    private final AccountService accountService;
    private final AccountChargeService accountChargeService;
    private final CardPaymentService cardPaymentService;
    private final AuthorizerAdapter authorizerAdapter;
    private final TransactionService transactionService;

    @Transactional
    @Override
    public ChargeResponse createCharge(ChargeRequest chargeRequest) {
        var charge = ChargeMapper.toEntity(chargeRequest);
        charge.setOriginator(currentUser());
        var cpf = LoginUtils.normalizeCpf(chargeRequest.getRecipientCpf());
        charge.setRecipient(findRecipientByCpf(cpf));
        charge.setStatus(ChargeStatus.PENDING);
        validateifOriginatorIsNotRecipient(charge.getOriginator(), charge.getRecipient());
        log.info("Creating charge to recipient's CPF: {}", charge.getRecipient().getCpf());
        chargeRepository.save(charge);
        return ChargeMapper.toDto(charge);
    }

    @Override
    public ChargeResponse paidWithBalance(UUID chargeId) {
        return payCharge(chargeId, charge -> {
            var originatorId = charge.getOriginator().getUserId();
            var recipientId = charge.getRecipient().getUserId();
            var amount = charge.getAmount();
            accountService.payWithBalance(amount);
            accountChargeService.creditBalance(originatorId, amount);
            charge.setPaymentMethod(PaymentMethod.BALANCE);

            var recipient = accountChargeService.findAccountById(recipientId);
            var originator = accountChargeService.findAccountById(originatorId);
            transactionService.registerBalancePaid(
                amount, 
                recipient, 
                originator, 
                charge, 
                TransactionType.PAYMENT,
                TransactionStatus.SUCCESS
            );
        });
    }

    @Override
    public ChargeResponse paidWithCard(UUID chargeId, CardPaymentRequest card) {
        return payCharge(chargeId, charge -> {
            var cardModel = cardPaymentService.processCardPayment(card, charge);
            charge.setPaymentMethod(PaymentMethod.CARD);

            transactionService.registerCardPaid(
                charge.getAmount(), 
                charge,
                cardModel, 
                TransactionType.PAYMENT,
                TransactionStatus.SUCCESS
            );
        });
    }

    @Override
    public ChargeResponse cancelCardCharge(UUID chargeId) {
        return cancelCharge(chargeId, PaymentMethod.CARD, charge -> {
                authorizerAdapter.isAuthorizedTransaction(
                charge.getChargeId(),
                charge.getAmount(),
                TransactionType.REFUND
            );
            transactionService.registerCardRefund(
                charge.getAmount(), 
                charge, 
                TransactionType.REFUND,
                TransactionStatus.SUCCESS
            );
            log.info("Card charge {} refunded successfully", charge.getChargeId());
        });
    }

    @Override
    public ChargeResponse cancelBalanceCharge(UUID chargeId) {
        return cancelCharge(chargeId, PaymentMethod.BALANCE, charge -> {
            var originatorId = charge.getOriginator().getUserId();
            var recipientId = charge.getRecipient().getUserId();
            var amount = charge.getAmount();
            accountChargeService.debitBalance(originatorId, amount);
            accountChargeService.creditBalance(recipientId, amount);

            var recipient = accountChargeService.findAccountById(recipientId);
            var originator = accountChargeService.findAccountById(originatorId);
            transactionService.registerBalanceRefund(
                amount, 
                originator, 
                recipient, 
                charge, 
                TransactionType.REFUND,
                TransactionStatus.SUCCESS
            );
            log.info("Balance charge {} refunded successfully", charge.getChargeId());
        });
    }

    @Override
    public Page<ChargeResponse> listSentCharges(Integer page, Integer size, String direction) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        var pageable = PageRequest.of(page, size, Sort.by(sortDirection, "originator"));
        var user = currentUser();
        log.info("Listing all charges sent by user: {}", user.getCpf());
        return chargeRepository.findByOriginator(user, pageable).map(ChargeMapper::toDto);
    }

    @Override
    public Page<ChargeResponse> listReceivedCharges(Integer page, Integer size, String direction) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
		var pageable = PageRequest.of(page, size, Sort.by(sortDirection, "recipient"));
        var user = currentUser();
        log.info("Listing all charges received by user: {}", user.getCpf());
        return chargeRepository.findByRecipient(user, pageable).map(ChargeMapper::toDto);
    }

    @Override
    public Page<ChargeResponse> listReceivedChargesAndStatus(Integer page, Integer size, String direction, String status) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
		var pageable = PageRequest.of(page, size, Sort.by(sortDirection, "recipient"));
        var user = currentUser();
        log.info("Listing all charges received by user: {}", user.getCpf());
        return chargeRepository.findByRecipientAndStatus(user, ChargeStatus.valueOf(status.toUpperCase()), pageable).map(ChargeMapper::toDto);
    }

    @Override
    public Page<ChargeResponse> listSentChargesAndStatus(Integer page, Integer size, String direction, String status) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
		var pageable = PageRequest.of(page, size, Sort.by(sortDirection, "originator"));
        var user = currentUser();
        log.info("Listing all charges sent by user: {}", user.getCpf());
        return chargeRepository.findByOriginatorAndStatus(user, ChargeStatus.valueOf(status.toUpperCase()), pageable).map(ChargeMapper::toDto);
    }

    private UserModel findRecipientByCpf(String cpf) {
        return userService.findUserByCpf(cpf);
    } 
    
    private void validateifOriginatorIsNotRecipient(UserModel originator, UserModel recipient) {
        if (originator.getCpf().equals(recipient.getCpf())) {
            log.error("The originator cannot be the recipient of the charge");
            throw new ValidationException("The originator cannot be the recipient of the charge");
        }
    }

    private UserModel currentUser() {
        return authentication.getCurrentUser();
    }

    private Charge findByChargeIdAndRecipientId(UUID chargeId, UUID recipientId) {
        log.info("Searching for charge with ID: {} and recipient ID: {}", chargeId, recipientId);
        return chargeRepository.findByChargeIdAndRecipientId(chargeId, recipientId).orElseThrow(() -> {
            log.error("Charge with ID {} and recipient ID {} not found", chargeId, recipientId);
            return new ObjectNotFoundException("Charge not found");
        });
    }

    private Charge findByChargeIdAndOriginatortId(UUID chargeId, UUID originatorId) {
        log.info("Searching for charge with ID: {} and originator ID: {}", chargeId, originatorId);
        return chargeRepository.findByChargeIdAndOriginatortId(chargeId, originatorId).orElseThrow(() -> {
            log.error("Charge with ID {} and originator ID {} not found", chargeId, originatorId);
            return new ObjectNotFoundException("Charge not found");
        });
    }

    private void VerifyingChargeIsPending(Charge charge) {
        if (charge.getStatus() != ChargeStatus.PENDING) {
            log.error("Charge with ID {} is not pending", charge.getChargeId());
            throw new ValidationException("Charge is not pending");
        }
    }

    @Transactional
    private ChargeResponse payCharge(UUID chargeId, PaymentAction action) {
        var user = findRecipientByCpf(currentUser().getCpf());
        var charge = findByChargeIdAndRecipientId(chargeId, user.getUserId());
        VerifyingChargeIsPending(charge);
        // Executa a ação específica do pagamento (saldo ou cartão)
        action.execute(charge);
        charge.setStatus(ChargeStatus.PAID);
        charge.setPaidAt(LocalDateTime.now());
        log.info("Creating charge to recipient's CPF: {}", charge.getRecipient().getCpf());
        chargeRepository.save(charge);
        return ChargeMapper.toDto(charge);
    }

    @Transactional
    private ChargeResponse cancelCharge(UUID chargeId, PaymentMethod paymentMethod, CancellationAction action) {
        var user = findRecipientByCpf(currentUser().getCpf());
        var charge = findByChargeIdAndOriginatortId(chargeId, user.getUserId());
        if (charge.getStatus() == ChargeStatus.CANCELED) {
            throw new ValidationException("Charge is already canceled.");
        }

        switch (charge.getStatus()) {
            case PENDING -> {
                charge.setStatus(ChargeStatus.CANCELED);
                charge.setCanceledAt(LocalDateTime.now());
                log.info("Pending charge {} canceled by user {}", chargeId, user.getCpf());
            }
            case PAID -> {
                validatePaymentMethod(charge, paymentMethod);
                // Executa a ação específica do cancelamento (saldo ou cartão)
                action.execute(charge);
                charge.setStatus(ChargeStatus.CANCELED);
                charge.setCanceledAt(LocalDateTime.now());
                log.info("Paid charge {} canceled by user {}", chargeId, user.getCpf());
            }
            default -> throw new ValidationException("Only pending or paid charges can be canceled.");
        }
        chargeRepository.save(charge);
        return ChargeMapper.toDto(charge);
    }

    private void validatePaymentMethod(Charge charge, PaymentMethod method) {
        if (charge.getPaymentMethod() != method) {
            throw new ValidationException("Invalid cancellation method. This charge was paid with " + charge.getPaymentMethod()
            );
        }
    }
}