package br.com.nimble.gateway.payment.api.v1.mapper;

import br.com.nimble.gateway.payment.api.v1.dto.response.AccountResponse;
import br.com.nimble.gateway.payment.api.v1.dto.response.AccountSummaryResponse;
import br.com.nimble.gateway.payment.domain.model.Account;
import br.com.nimble.gateway.payment.domain.model.UserModel;

public class AccountMapper {
    
    private AccountMapper() {}

    public static Account toEntity(UserModel user) {
        var account = new Account();
        account.setUser(user);
        account.deposit(account.getBalance());
        return account;
    }

    public static AccountResponse toDto(Account account) {
        var response = new AccountResponse();
        response.setAccountId(account.getAccountId().toString());
        response.setBalance(account.getBalance());
        return response;
    }

    public static AccountSummaryResponse toSummaryDto(Account account) {
        var response = new AccountSummaryResponse();
        response.setAccountId(account.getAccountId());
        response.setOwnerName(account.getUser().getFullName());
        return response;
    }
}