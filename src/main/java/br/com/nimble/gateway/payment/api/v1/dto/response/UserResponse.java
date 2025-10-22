package br.com.nimble.gateway.payment.api.v1.dto.response;

import java.io.Serializable;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Represents a User")
public class UserResponse implements Serializable {

    @Schema(description = "Unique identifier of the user", example = "1")
    private UUID userId;

    @Schema(description = "Full name of the person", example = "Robert Martin")
    private String fullName;

    @Schema(description = "Email of the user.", example = "robert@example.com")
    private String email;

    @Schema(description = "CPF of the user.", example = "123.456.789-09")
    private String cpf;
}