package br.com.nimble.gateway.payment.service;

import java.math.BigDecimal;

import br.com.nimble.gateway.payment.api.v1.dto.response.AccountResponse;
import br.com.nimble.gateway.payment.domain.model.Account;
import br.com.nimble.gateway.payment.domain.model.UserModel;

public interface AccountService {
    
    Account createAccount(UserModel user);

    AccountResponse deposit(BigDecimal amount);

    String payWithBalance(BigDecimal amount);
}