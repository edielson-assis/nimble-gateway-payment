package br.com.nimble.gateway.payment.api.v1.dto.request;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Request DTO for card payment processing.")
public class CardPaymentRequest implements Serializable {
    
    @Schema(description = "Card number.", example = "4111111111111111", required = true)
    @NotNull(message = "Card number is required")
    @Pattern(regexp = "\\d{13,19}", message = "Card number must be between 13 and 19 digits")
    private String cardNumber;

    @Schema(description = "Card holder name.", example = "John Doe", required = true)
    @NotNull(message = "Card holder name is required")
    private String cardHolder;

    @Schema(description = "Card expiration date.", example = "12/25", required = true)
    @NotNull(message = "Card expiration date is required")
    private String expirationDate;

    @Schema(description = "Card CVV.", example = "123", required = true)
    @NotNull(message = "Card CVV is required")
    @Pattern(regexp = "\\d{3,4}", message = "Card CVV must be 3 or 4 digits")
    private String cvv;
}