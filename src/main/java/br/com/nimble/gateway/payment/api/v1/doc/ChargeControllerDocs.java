package br.com.nimble.gateway.payment.api.v1.doc;

import org.springframework.http.ResponseEntity;

import br.com.nimble.gateway.payment.api.v1.dto.request.ChargeRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.ChargeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Charges", description = "Endpoints for Charge Management")
public interface ChargeControllerDocs {

    static final String SECURITY_SCHEME_KEY = "bearer-key";
    
    @Operation(
        security = {@SecurityRequirement(name = SECURITY_SCHEME_KEY)},
        summary = "Creates a new charge",
        description = "Creates a new charge and returns the charge details",
        tags = {"Charges"},
        responses = {
            @ApiResponse(responseCode = "201", description = "Charge created successfully",
				content = @Content(schema = @Schema(implementation = ChargeResponse.class))),
			@ApiResponse(responseCode = "400", description = "Bad request - Something is wrong with the request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - You do not have permission to access this resource",  content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found - User not found", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Server Error - Server error", content = @Content)
		}
	)
    ResponseEntity<ChargeResponse> createCharge(ChargeRequest chargeRequest);
}