package br.com.nimble.gateway.payment.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountChargeService {
    
    void creditBalance(UUID userId, BigDecimal amount);
}