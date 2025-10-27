package br.com.nimble.gateway.payment.api.v1.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.nimble.gateway.payment.domain.model.enums.ChargeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Represents a Charge")
public class ChargeResponse implements Serializable {

    @Schema(description = "Unique identifier of the charge", example = "1")
    private UUID chargeId;

    @Schema(description = "Originator of the charge", example = "Robert Martin")
    private UserResponse originator;

    @Schema(description = "Recipient of the charge", example = "John Doe")
    private UserResponse recipient;

    @Schema(description = "Status of the charge", example = "PENDING")
    private ChargeStatus chargeStatus;

    @Schema(description = "Amount of the charge", example = "100.00")
    private BigDecimal amount;

    @Schema(description = "Description of the charge", example = "Payment for order #12345")
    private String description;

    @Schema(description = "Timestamp when the charge was created", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the charge was paid", example = "2024-01-01T12:00:00")
    private LocalDateTime paidAt;

    @Schema(description = "Timestamp when the charge was canceled", example = "2024-01-01T12:00:00")
    private LocalDateTime canceledAt;
}