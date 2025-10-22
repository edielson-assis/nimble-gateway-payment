package br.com.nimble.gateway.payment.api.v1.doc;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import br.com.nimble.gateway.payment.api.v1.dto.request.UserPutPasswordRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.UserPutRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Users", description = "Endpoints for Managing User")
public interface UserControllerDocs {

    static final String SECURITY_SCHEME_KEY = "bearer-key";

    @Operation(
        security = {@SecurityRequirement(name = SECURITY_SCHEME_KEY)},
        summary = "See all users",
		description = "See all users - admin and moderador",
		tags = {"Users"},
		responses = {
			@ApiResponse(responseCode = "200", description = "All users",
				content = @Content(schema = @Schema(implementation = UserResponse.class))),
			@ApiResponse(responseCode = "400", description = "Bad request - Something is wrong with the request", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden - You do not have permission to access this resource",  content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Server Error - Server error", content = @Content)
		}
	)
    ResponseEntity<Page<UserResponse>> getAllUsers(Integer page, Integer size, String direction);

    @Operation(
        security = {@SecurityRequirement(name = SECURITY_SCHEME_KEY)},
        summary = "See one user",
		description = "See one user by ID",
		tags = {"Users"},
		responses = {
			@ApiResponse(responseCode = "200", description = "User found",
				content = @Content(schema = @Schema(implementation = UserResponse.class))),
			@ApiResponse(responseCode = "400", description = "Bad request - Something is wrong with the request", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden - You do not have permission to access this resource",  content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found - User not found", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Server Error - Server error", content = @Content)
		}
	)
    ResponseEntity<UserResponse> getOneUser(UUID userId);

    @Operation(
        security = {@SecurityRequirement(name = SECURITY_SCHEME_KEY)},
        summary = "Update user",
		description = "Update user by ID",
		tags = {"Users"},
		responses = {
			@ApiResponse(responseCode = "200", description = "User updated",
				content = @Content(schema = @Schema(implementation = UserResponse.class))),
			@ApiResponse(responseCode = "400", description = "Bad request - Something is wrong with the request", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden - You do not have permission to access this resource",  content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found - User not found", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Server Error - Server error", content = @Content)
		}
	)
    ResponseEntity<UserResponse> updateUser(UUID userId, UserPutRequest userDto);

    @Operation(
        security = {@SecurityRequirement(name = SECURITY_SCHEME_KEY)},
        summary = "Update user password",
		description = "Update user password by ID",
		tags = {"Users"},
		responses = {
			@ApiResponse(responseCode = "200", description = "User password updated",
				content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "Bad request - Something is wrong with the request", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden - You do not have permission to access this resource",  content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found - User not found", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Server Error - Server error", content = @Content)
		}
	)
    ResponseEntity<String> updatePassword(UserPutPasswordRequest userDto);

    @Operation(
        security = {@SecurityRequirement(name = SECURITY_SCHEME_KEY)},
        summary = "Disable user",
		description = "Disable user by ID",
		tags = {"Users"},
		responses = {
			@ApiResponse(responseCode = "204", description = "User disabled"),
			@ApiResponse(responseCode = "400", description = "Bad request - Something is wrong with the request", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden - You do not have permission to access this resource",  content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found - User not found", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Server Error - Server error", content = @Content)
		}
	)
    ResponseEntity<Void> disableUser(UUID userId);
}