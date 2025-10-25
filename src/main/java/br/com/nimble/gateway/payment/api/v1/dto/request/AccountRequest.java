package br.com.nimble.gateway.payment.api.v1.dto.request;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Request DTO for account operations such as deposit.")
public class AccountRequest implements Serializable {

    @Schema(description = "Amount to be charged.", example = "150.75", required = true)
    @Positive(message = "Amount must be positive")
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
}