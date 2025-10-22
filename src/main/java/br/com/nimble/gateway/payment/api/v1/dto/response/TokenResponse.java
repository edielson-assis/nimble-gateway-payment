package br.com.nimble.gateway.payment.api.v1.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representation of a JWT Token")
public record TokenResponse(

    @Schema(description = "Access token provided for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
    String accessToken
) {}