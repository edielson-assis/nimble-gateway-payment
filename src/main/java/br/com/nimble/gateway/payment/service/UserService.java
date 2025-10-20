package br.com.nimble.gateway.payment.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import br.com.nimble.gateway.payment.api.v1.dto.request.UserSignupRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.UserResponse;
import br.com.nimble.gateway.payment.domain.model.UserModel;

public interface UserService {

    UserResponse saveUser(UserSignupRequest userDto);

    Page<UserResponse> findAllUsers(Integer page, Integer size, String direction, Specification<UserModel> spec);

    UserResponse findUser(UUID userId);

    void disableUser(UUID userId);
}