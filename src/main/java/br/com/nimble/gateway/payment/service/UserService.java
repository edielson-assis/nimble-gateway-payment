package br.com.nimble.gateway.payment.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import br.com.nimble.gateway.payment.api.v1.dto.request.UserPutPasswordRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.UserPutRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.UserSigninRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.UserSignupRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.TokenAndRefreshTokenResponse;
import br.com.nimble.gateway.payment.api.v1.dto.response.TokenResponse;
import br.com.nimble.gateway.payment.api.v1.dto.response.UserResponse;

public interface UserService {

    UserResponse saveCommonUser(UserSignupRequest userDto);

    UserResponse saveModerator(UserSignupRequest userDto);

    Page<UserResponse> findAllUsers(Integer page, Integer size, String direction);

    UserResponse findUser(UUID userId);

    UserResponse updateUser(UUID userId, UserPutRequest userDto);

    String updateUserPassword(UserPutPasswordRequest userDto);

    TokenAndRefreshTokenResponse signin(UserSigninRequest userDto);

    TokenResponse refreshToken(String username, String refreshToken);

    void disableUser(UUID userId);
}