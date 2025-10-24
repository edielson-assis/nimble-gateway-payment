package br.com.nimble.gateway.payment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.nimble.gateway.payment.api.v1.dto.request.AccountRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.AccountResponse;
import br.com.nimble.gateway.payment.api.v1.dto.response.AuthorizerResponse;
import br.com.nimble.gateway.payment.api.v1.mapper.AccountMapper;
import br.com.nimble.gateway.payment.domain.exception.ObjectNotFoundException;
import br.com.nimble.gateway.payment.domain.model.Account;
import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.repository.AccountRepository;
import br.com.nimble.gateway.payment.integration.AuthorizerService;
import br.com.nimble.gateway.payment.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AuthorizerService authorizerService;

    @Transactional
    @Override
    public Account createForUser(UserModel user) {
        Account account = AccountMapper.toEntity(user);
        log.info("Creating account for userId: {}", user.getUserId());
        return accountRepository.save(account);
    }

    @Transactional
    @Override
    public AccountResponse deposit(AccountRequest accountRequest) {
        var account = findAccountById(accountRequest.getAccountId());
        account.deposit(accountRequest.getAmount());
        isAuthorizedTransaction();
        log.info("Updating account balance for accountId: {}. New balance: {}", accountRequest.getAccountId(), account.getBalance());
        accountRepository.save(account);
        return AccountMapper.toDto(account);
    }

    @Transactional
    @Override
    public AccountResponse withdraw(AccountRequest accountRequest) {
        var account = findAccountById(accountRequest.getAccountId());
        if (!account.withdraw(accountRequest.getAmount())) {
            log.error("Insufficient funds for withdrawal of amount: {} from accountId: {}", accountRequest.getAmount(), accountRequest.getAccountId());
            throw new IllegalArgumentException("Insufficient funds for withdrawal");
        }
        log.info("Withdrawing amount: {} from accountId: {}. New balance: {}", accountRequest.getAmount(), accountRequest.getAccountId(), account.getBalance());
        isAuthorizedTransaction();
        accountRepository.save(account);
        return AccountMapper.toDto(account);
    }

    private Account findAccountById(UUID accountId) {
        log.info("Verifying the account's Id: {}", accountId);
        return accountRepository.findById(accountId).orElseThrow(() -> {
            log.error("Account not found for id: {}", accountId);
            return new ObjectNotFoundException("Account not found for id: " + accountId);
        });
    }

    private AuthorizerResponse isAuthorizedTransaction() {
        var authorized = authorizerService.authorizeTransaction();
        if (!authorized.data().authorized()) {
            log.info("Transaction not authorized by external service.");
            throw new IllegalArgumentException("Transaction not authorized");
        }
        return authorized;
    }
}