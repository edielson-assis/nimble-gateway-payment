package br.com.nimble.gateway.payment.api.v1.mapper;

import org.springframework.beans.BeanUtils;

import br.com.nimble.gateway.payment.api.v1.dto.request.UserPutPasswordRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.UserPutRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.UserSignupRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.UserResponse;
import br.com.nimble.gateway.payment.domain.model.RoleModel;
import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.model.enums.UserStatus;
import br.com.nimble.gateway.payment.domain.model.enums.UserType;

public class UserMapper {
    
    private UserMapper() {}

    public static UserModel toEntity(UserSignupRequest userDto, UserType userType, RoleModel role) {
        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(userType);
        userModel.getPermissions().add(role);
        return userModel;
    }

    public static UserModel toEntity(UserModel userModel, UserType userType, RoleModel role) {
        userModel.setUserType(userType);
        userModel.getPermissions().add(role);
        return userModel;
    }

    public static UserModel toEntity(UserModel userModel, UserPutPasswordRequest userDto) {
        userModel.setPassword(userDto.getPassword());
        return userModel;
    }

    public static UserModel toEntity(UserModel userModel, UserPutRequest userDto) {
        userModel.setFullName(userDto.getFullName());
        return userModel;
    }

    public static UserResponse toDto(UserModel userModel) {
        var userResponse = new UserResponse();
        BeanUtils.copyProperties(userModel, userResponse);
        return userResponse;
    }
}