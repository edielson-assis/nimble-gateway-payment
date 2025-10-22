package br.com.nimble.gateway.payment.api.v1.doc;

import org.springframework.http.ResponseEntity;

import br.com.nimble.gateway.payment.api.v1.dto.request.UserSigninRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.UserSignupRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.TokenAndRefreshTokenResponse;
import br.com.nimble.gateway.payment.api.v1.dto.response.TokenResponse;
import br.com.nimble.gateway.payment.api.v1.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication", description = "Endpoints for User Authentication and Registration")
public interface AuthenticationControllerDocs {
    
    @Operation(
        summary = "Adds a new user",
		description = "Adds a new user",
		tags = {"Authentication"},
		responses = {
			@ApiResponse(responseCode = "201", description = "Created user",
				content = @Content(schema = @Schema(implementation = UserResponse.class))),
			@ApiResponse(responseCode = "400", description = "Bad request - Something is wrong with the request", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden - Authentication problem",  content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Server Error - Server error", content = @Content)
		}
	)
    ResponseEntity<UserResponse> signup(UserSignupRequest userRequest);

    @Operation(
        summary = "Authenticates a user",
		description = "Authenticates a user and returns a token",
		tags = {"Authentication"},
		responses = {
			@ApiResponse(responseCode = "200", description = "Authenticated user",
				content = @Content(schema = @Schema(implementation = TokenAndRefreshTokenResponse.class))),
			@ApiResponse(responseCode = "400", description = "Bad request - Something is wrong with the request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid email or password", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Server Error - Server error", content = @Content)
		}
	)
    ResponseEntity<TokenAndRefreshTokenResponse> signin(UserSigninRequest userRequest);

    @Operation(
        summary = "Updates a Token",
		description = "Refresh token for authenticated user and returns a token",
		tags = {"Authentication"},
		responses = {
			@ApiResponse(responseCode = "200", description = "Updated token", 
				content = @Content(schema = @Schema(implementation = TokenResponse.class))),
			@ApiResponse(responseCode = "400", description = "Bad request - Something is wrong with the request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authentication problem",  content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found - User not found", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Server Error - Server error", content = @Content)
		}
	)
    ResponseEntity<TokenResponse> refreshToken(String username, String refreshToken);
}