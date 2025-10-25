package br.com.nimble.gateway.payment.service.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.nimble.gateway.payment.api.v1.dto.response.AccountResponse;
import br.com.nimble.gateway.payment.api.v1.mapper.AccountMapper;
import br.com.nimble.gateway.payment.config.security.context.AuthenticatedUserProvider;
import br.com.nimble.gateway.payment.domain.exception.ObjectNotFoundException;
import br.com.nimble.gateway.payment.domain.model.Account;
import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionType;
import br.com.nimble.gateway.payment.domain.repository.AccountRepository;
import br.com.nimble.gateway.payment.integration.AuthorizerAdapter;
import br.com.nimble.gateway.payment.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AuthorizerAdapter authorizerAdapter;
    private final AuthenticatedUserProvider authentication;

    @Transactional
    @Override
    public Account createAccount(UserModel user) {
        Account account = AccountMapper.toEntity(user);
        log.info("Creating account for userId: {}", user.getUserId());
        return accountRepository.save(account);
    }

    @Transactional
    @Override
    public AccountResponse deposit(BigDecimal amount) {
        var account = findAccountById(currentUser());
        var accountResponse = AccountMapper.toDto(account);
        authorizerAdapter.isAuthorizedTransaction(accountResponse, amount, TransactionType.DEPOSIT);
        account.deposit(amount);
        log.info("Updating account balance for userId: {}. New balance: {}", currentUser(), account.getBalance());
        accountRepository.save(account);
        return accountResponse;
    }

    @Transactional
    @Override
    public String payWithBalance(BigDecimal amount) {
        var account = findAccountById(currentUser());
        if (!account.payWithBalance(amount)) {
            log.error("Insufficient funds for payment of amount: {} from accountId: {}", amount, account.getAccountId());
            throw new IllegalArgumentException("Insufficient funds for payment");
        }
        log.info("Withdrawing amount: {} from accountId: {}. New balance: {}", amount, account.getAccountId(), account.getBalance());
        var accountResponse = AccountMapper.toDto(account);
        authorizerAdapter.isAuthorizedTransaction(accountResponse, amount, TransactionType.PAYMENT);
        accountRepository.save(account);
        return "Payment of " + amount + " processed successfully.";
    }

    private Account findAccountById(UUID accountId) {
        log.info("Verifying the account's Id: {}", accountId);
        return accountRepository.findByUserId(accountId).orElseThrow(() -> {
            log.error("Account not found for id: {}", accountId);
            return new ObjectNotFoundException("Account not found for id: " + accountId);
        });
    }

    private UUID currentUser() {
        return authentication.getCurrentUser().getUserId();
    }
}