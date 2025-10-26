package br.com.nimble.gateway.payment.api.v1.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.nimble.gateway.payment.domain.model.enums.TransactionStatus;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Represents a financial transaction between accounts, such as a payment or refund related to a charge.")
public class TransactionResponse implements Serializable {

    @Schema(description = "Unique identifier of the transaction.", example = "a3c3e8e5-97b9-4c6e-87c5-2a58d00a4c92")
    private UUID transactionId;

    @Schema(description = "Type of transaction performed.", example = "PAYMENT", allowableValues = { "DEPOSIT", "PAYMENT", "REFUND" })
    private TransactionType type;

    @Schema(description = "Total amount involved in the transaction.", example = "250.75")
    private BigDecimal amount;

    @Schema(description = "Account from which the transaction originated (payer).")
    private AccountSummaryResponse fromAccount;

    @Schema(description = "Destination account of the transaction (receiver).")
    private AccountSummaryResponse toAccount;

    @Schema(description = "Associated charge, if applicable (for payments or refunds).")
    private ChargeResponse charge;

    @Schema( description = "Current status of the transaction.", example = "SUCCESS", allowableValues = { "PENDING", "SUCCESS", "FAILED" })
    private TransactionStatus status;

    @Schema( description = "Date and time when the transaction was created.", example = "2025-10-24T14:32:15")
    private LocalDateTime createdAt;
}