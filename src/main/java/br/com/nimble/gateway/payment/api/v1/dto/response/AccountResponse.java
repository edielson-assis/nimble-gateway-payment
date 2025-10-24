package br.com.nimble.gateway.payment.api.v1.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Represents an Account")
public class AccountResponse implements Serializable {

    @Schema(description = "Unique identifier of the account", example = "1")
    private String accountId;

    @Schema(description = "Current balance of the account", example = "100.00")
    private BigDecimal balance;
}