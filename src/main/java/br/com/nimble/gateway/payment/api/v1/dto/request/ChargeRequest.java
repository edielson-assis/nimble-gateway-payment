package br.com.nimble.gateway.payment.api.v1.dto.request;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for creating a charge.")
public class ChargeRequest implements Serializable {
    
    @Schema(description = "CPF of the recipient.", example = "123.456.789-00", required = true)
    @NotBlank(message = "Recipient CPF is required")
    private String recipientCpf;

    @Schema(description = "Amount to be charged.", example = "150.75", required = true)
    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @Schema(description = "Description of the charge.", example = "Payment for services")
    private String description;
}