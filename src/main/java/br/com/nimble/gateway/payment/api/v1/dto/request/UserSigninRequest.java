package br.com.nimble.gateway.payment.api.v1.dto.request;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Represents the User's Signin")
public class UserSigninRequest implements Serializable {

    @Schema(description = "Email or CPF of the user.", example = "robert123@example.com")
    private String emailOrCpf;

    @Schema(description = "Password of the user.", example = "P@ssw0rd!")
    private String password;
}