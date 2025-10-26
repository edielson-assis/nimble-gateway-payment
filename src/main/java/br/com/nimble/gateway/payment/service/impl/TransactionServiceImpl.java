package br.com.nimble.gateway.payment.service.impl;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.nimble.gateway.payment.api.v1.dto.response.TransactionResponse;
import br.com.nimble.gateway.payment.api.v1.mapper.TransactionMapper;
import br.com.nimble.gateway.payment.config.security.context.AuthenticatedUserProvider;
import br.com.nimble.gateway.payment.domain.model.Account;
import br.com.nimble.gateway.payment.domain.model.CardPayment;
import br.com.nimble.gateway.payment.domain.model.Charge;
import br.com.nimble.gateway.payment.domain.model.Transaction;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionStatus;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionType;
import br.com.nimble.gateway.payment.domain.repository.TransactionRepository;
import br.com.nimble.gateway.payment.service.TransactionProvider;
import br.com.nimble.gateway.payment.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService, TransactionProvider {

    private final TransactionRepository transactionRepository;
    private final AuthenticatedUserProvider authentication;

    @Transactional
    @Override
    public Transaction registerBalancePaid(BigDecimal amount, Account originator, Account recipient, Charge charge, TransactionType type, TransactionStatus status) {
        var transaction = TransactionMapper.toEntity(amount, originator, recipient, charge, type, status);
        log.info("Registing paid to charge ID: {}", charge.getChargeId());
        return transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public Transaction registerCardPaid(BigDecimal amount, Charge charge, CardPayment card, TransactionType type, TransactionStatus status) {
        var transaction = TransactionMapper.toEntity(amount, charge, type, status);
        log.info("Registing paid to charge ID: {}", charge.getChargeId());
        return transactionRepository.save(transaction);
    }    

    @Transactional
    @Override
    public Transaction registerBalanceRefund(BigDecimal amount, Account originator, Account recipient, Charge charge, TransactionType type, TransactionStatus status) {
        var transaction = TransactionMapper.toEntity(amount, originator, recipient, charge, type, status);
        log.info("Registing refund to charge ID: {}", charge.getChargeId());
        return transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public Transaction registerCardRefund(BigDecimal amount, Charge charge, TransactionType type, TransactionStatus status) {
        var transaction = TransactionMapper.toEntity(amount, charge, type, status);
        log.info("Registing refund to charge ID: {}", charge.getChargeId());
        return transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public Transaction registerDeposit(BigDecimal amount, Account account, TransactionType type, TransactionStatus status) {
        var transaction = TransactionMapper.toEntity(amount, account, type, status);
        log.info("Registing account balance for accountId: {}", account.getAccountId());
        return transactionRepository.save(transaction);
    }

    @Override
    public Page<TransactionResponse> listAllTransactionByUser(Integer page, Integer size, String direction) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        var pageable = PageRequest.of(page, size, Sort.by(sortDirection, "createdAt"));
        var user = authentication.getCurrentUser();
        log.info("Listing all transactions by user: {}", user.getCpf());
        return transactionRepository.findByUserTransaction(user.getUserId(), pageable).map(TransactionMapper::toDto);
    }
}