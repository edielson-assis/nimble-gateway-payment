package br.com.nimble.gateway.payment.service;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.nimble.gateway.payment.domain.model.Account;

public interface AccountChargeService {
    
    void creditBalance(UUID userId, BigDecimal amount);

    void debitBalance(UUID userId, BigDecimal amount);

    Account findAccountById(UUID accountId);
}