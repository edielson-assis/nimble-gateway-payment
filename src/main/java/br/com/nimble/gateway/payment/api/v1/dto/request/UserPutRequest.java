package br.com.nimble.gateway.payment.api.v1.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPutRequest implements Serializable {
    
    @NotBlank(message = "FullName is required")
    private String fullName;
}