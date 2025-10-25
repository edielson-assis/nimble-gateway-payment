package br.com.nimble.gateway.payment.service;

import br.com.nimble.gateway.payment.api.v1.dto.request.UserSigninRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.UserSignupRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.TokenAndRefreshTokenResponse;
import br.com.nimble.gateway.payment.api.v1.dto.response.TokenResponse;
import br.com.nimble.gateway.payment.api.v1.dto.response.UserResponse;

public interface UserService {

    UserResponse saveUser(UserSignupRequest userDto);

    TokenAndRefreshTokenResponse signin(UserSigninRequest userDto);

    TokenResponse refreshToken(String username, String refreshToken);
}