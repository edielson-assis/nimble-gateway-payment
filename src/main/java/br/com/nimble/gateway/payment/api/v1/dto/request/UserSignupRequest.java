package br.com.nimble.gateway.payment.api.v1.dto.request;

import java.io.Serializable;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequest implements Serializable {

    @NotBlank(message = "FullName is required")
    private String fullName;

    @NotBlank(message = "CPF is required")
    @CPF(message = "Invalid CPF")
    private String cpf;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}