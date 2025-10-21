package br.com.nimble.gateway.payment.api.v1.dto.response;

public record TokenAndRefreshTokenResponse(String accessToken, String refreshToken) {}