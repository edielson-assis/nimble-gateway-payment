package br.com.nimble.gateway.payment.service;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.nimble.gateway.payment.api.v1.dto.response.AccountResponse;

public interface AccountChargeService {
    
    AccountResponse creditBalance(UUID userId, BigDecimal amount);
}