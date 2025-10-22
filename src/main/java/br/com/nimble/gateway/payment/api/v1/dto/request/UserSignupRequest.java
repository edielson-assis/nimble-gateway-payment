package br.com.nimble.gateway.payment.api.v1.dto.request;

import java.io.Serializable;

import org.hibernate.validator.constraints.br.CPF;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Represents the User's Signup")
public class UserSignupRequest implements Serializable {

    @Schema(description = "Full name of the person", example = "Robert Martin", maxLength = 150, required = true)
    @NotBlank(message = "FullName is required")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s']+$", message = "Only letters should be typed")
    private String fullName;

    @Schema(description = "CPF of the user.", example = "123.456.789-09", required = true)
    @NotBlank(message = "CPF is required")
    @CPF(message = "Invalid CPF")
    private String cpf;

    @Schema(description = "Email of the user.", example = "robert@example.com", maxLength = 100, required = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @Schema(description = "Password of the user.", example = "P@ssw0rd!", maxLength = 255, required = true)
    @NotBlank(message = "Password is required")
    private String password;
}