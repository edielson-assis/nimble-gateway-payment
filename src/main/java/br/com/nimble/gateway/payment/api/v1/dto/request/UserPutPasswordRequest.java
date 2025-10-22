package br.com.nimble.gateway.payment.api.v1.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPutPasswordRequest implements Serializable {

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "OldPassword is required")
    private String oldPassword;
}