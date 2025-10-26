package br.com.nimble.gateway.payment.api.v1.dto.response;

import java.io.Serializable;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Summary view of an account involved in a transaction (without sensitive data).")
public class AccountSummaryResponse implements Serializable {

    @Schema(description = "Unique identifier of the account.", example = "f6c4e7b9-20b1-4f23-a8d3-98f5d8c3b2b7")
    private UUID accountId;

    @Schema(description = "Owner's full name.", example = "John Doe")
    private String ownerName;
}