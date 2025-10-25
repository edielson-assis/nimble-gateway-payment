package br.com.nimble.gateway.payment.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.nimble.gateway.payment.api.v1.dto.request.ChargeRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.ChargeResponse;
import br.com.nimble.gateway.payment.api.v1.mapper.ChargeMapper;
import br.com.nimble.gateway.payment.config.security.context.AuthenticatedUserProvider;
import br.com.nimble.gateway.payment.domain.exception.ValidationException;
import br.com.nimble.gateway.payment.domain.model.Charge;
import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.model.enums.ChargeStatus;
import br.com.nimble.gateway.payment.domain.model.enums.PaymentMethod;
import br.com.nimble.gateway.payment.domain.repository.ChargeRepository;
import br.com.nimble.gateway.payment.service.AccountChargeService;
import br.com.nimble.gateway.payment.service.AccountService;
import br.com.nimble.gateway.payment.service.ChargeService;
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

    @Transactional
    @Override
    public ChargeResponse paidChargeWithBalance(UUID chargeId) {
        var user = findRecipientByCpf(currentUser().getCpf());
        var charge = findByChargeIdAndRecipientId(chargeId, user.getUserId());
        VerifyingChargeIsPending(charge);
        accountService.payWithBalance(charge.getAmount());
        accountChargeService.creditBalance(charge.getOriginator().getUserId(), charge.getAmount());
        charge.setStatus(ChargeStatus.PAID);
        charge.setPaidAt(LocalDateTime.now());
        charge.setPaymentMethod(PaymentMethod.BALANCE);
        log.info("Creating charge to recipient's CPF: {}", charge.getRecipient().getCpf());
        chargeRepository.save(charge);
        return ChargeMapper.toDto(charge);
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
		var pageable = PageRequest.of(page, size, Sort.by(sortDirection, "recipient"));
        var user = currentUser();
        log.info("Listing all charges received by user: {}", user.getCpf());
        return chargeRepository.findByRecipientAndStatus(user, ChargeStatus.valueOf(status.toUpperCase()), pageable).map(ChargeMapper::toDto);
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
            return new ValidationException("Charge not found");
        });
    }

    private void VerifyingChargeIsPending(Charge charge) {
        if (charge.getStatus() != ChargeStatus.PENDING) {
            log.error("Charge with ID {} is not pending", charge.getChargeId());
            throw new ValidationException("Charge is not pending");
        }
    }
}