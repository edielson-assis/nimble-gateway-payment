package br.com.nimble.gateway.payment.api.v1.mapper;

import br.com.nimble.gateway.payment.domain.model.Account;
import br.com.nimble.gateway.payment.domain.model.UserModel;

public class AccountMapper {
    
    private AccountMapper() {}

    public static Account toEntity(UserModel user) {
        Account account = new Account();
        account.setUser(user);
        account.updateBalance(account.getBalance());
        return account;
    }
}