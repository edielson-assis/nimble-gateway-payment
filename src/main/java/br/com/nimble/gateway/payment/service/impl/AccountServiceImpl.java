package br.com.nimble.gateway.payment.service.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.nimble.gateway.payment.api.v1.mapper.AccountMapper;
import br.com.nimble.gateway.payment.domain.exception.ObjectNotFoundException;
import br.com.nimble.gateway.payment.domain.model.Account;
import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    @Override
    public Account createForUser(UserModel user) {
        Account account = AccountMapper.toEntity(user);
        log.info("Creating account for userId: {}", user.getUserId());
        return accountRepository.save(account);
    }

    @Transactional
    @Override
    public void updateBalance(UUID accountId, BigDecimal newBalance) {
        var account = findAccountById(accountId);
        account.updateBalance(newBalance);
        log.info("Updating account balance for accountId: {}. New balance: {}", accountId, account.getBalance());
        accountRepository.save(account);
    }

    @Transactional
    @Override
    public void withdraw(UUID accountId, BigDecimal amount) {
        var account = findAccountById(accountId);
        if (!account.withdraw(amount)) {
            log.error("Insufficient funds for withdrawal of amount: {} from accountId: {}", amount, accountId);
            throw new IllegalArgumentException("Insufficient funds for withdrawal");
        } 
        log.info("Withdrawing amount: {} from accountId: {}. New balance: {}", amount, accountId, account.getBalance());
        accountRepository.save(account);
    }

    private Account findAccountById(UUID accountId) {
        log.info("Verifying the account's Id: {}", accountId);
        return accountRepository.findById(accountId).orElseThrow(() -> {
            log.error("Account not found for id: {}", accountId);
            return new ObjectNotFoundException("Account not found for id: " + accountId);
        });
    }
}