package br.com.nimble.gateway.payment.api.v1.doc;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import br.com.nimble.gateway.payment.api.v1.dto.response.TransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Transactions", description = "Endpoints for Transaction Management")
public interface TransactionControllerDocs {

    static final String SECURITY_SCHEME_KEY = "bearer-key";
    
    @Operation(
        security = {@SecurityRequirement(name = SECURITY_SCHEME_KEY)},
        summary = "Lists all transactions",
        description = "Retrieves a paginated list of transactions by the authenticated user",
        tags = {"Transactions"},
        responses = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully",
                content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Something is wrong with the request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - You do not have permission to access this resource",  content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found - User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Server error", content = @Content)
        }
    )
    ResponseEntity<Page<TransactionResponse>> listAllTransactionByUser(Integer page, Integer size, String direction);
}