package br.com.nimble.gateway.payment.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserType {
    
    ADMIN("Admin"),
    MODERATOR("Moderator"),
    USER("User");

    private String type;
}