package br.com.nimble.gateway.payment.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    BLOCKED("Blocked");

    private String status;  
}