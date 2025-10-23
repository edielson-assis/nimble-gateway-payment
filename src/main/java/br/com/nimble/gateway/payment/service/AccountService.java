package br.com.nimble.gateway.payment.service;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.nimble.gateway.payment.domain.model.Account;
import br.com.nimble.gateway.payment.domain.model.UserModel;

public interface AccountService {
    
    Account createForUser(UserModel user);

    void updateBalance(UUID accountId, BigDecimal newBalance);

    void withdraw(UUID accountId, BigDecimal amount);
}