package br.com.nimble.gateway.payment.config.security.context;

import br.com.nimble.gateway.payment.api.v1.dto.request.UserSigninRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.TokenAndRefreshTokenResponse;
import br.com.nimble.gateway.payment.domain.model.UserModel;

public interface AuthenticatedUserProvider {
    
    TokenAndRefreshTokenResponse authenticateUser(UserSigninRequest userDto);

    UserModel getCurrentUser();
}