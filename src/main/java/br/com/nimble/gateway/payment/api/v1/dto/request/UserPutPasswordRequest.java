package br.com.nimble.gateway.payment.api.v1.dto.request;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for updating user password.")
public class UserPutPasswordRequest implements Serializable {

    @Schema(description = "Password of the user.", example = "P@ssw0rd!", required = true)
    @NotBlank(message = "Password is required")
    private String password;

    @Schema(description = "Old password of the user.", example = "OldP@ssw0rd!", required = true)
    @NotBlank(message = "OldPassword is required")
    private String oldPassword;
}