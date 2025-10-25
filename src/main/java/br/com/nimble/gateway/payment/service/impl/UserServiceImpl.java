package br.com.nimble.gateway.payment.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.nimble.gateway.payment.api.v1.dto.request.UserSigninRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.UserSignupRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.TokenAndRefreshTokenResponse;
import br.com.nimble.gateway.payment.api.v1.dto.response.TokenResponse;
import br.com.nimble.gateway.payment.api.v1.dto.response.UserResponse;
import br.com.nimble.gateway.payment.api.v1.mapper.UserMapper;
import br.com.nimble.gateway.payment.config.security.JwtTokenProvider;
import br.com.nimble.gateway.payment.config.security.context.AuthenticatedUserProvider;
import br.com.nimble.gateway.payment.domain.exception.ObjectNotFoundException;
import br.com.nimble.gateway.payment.domain.exception.ValidationException;
import br.com.nimble.gateway.payment.domain.model.RoleModel;
import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.model.enums.UserType;
import br.com.nimble.gateway.payment.domain.repository.UserRepository;
import br.com.nimble.gateway.payment.service.AccountService;
import br.com.nimble.gateway.payment.service.RoleService;
import br.com.nimble.gateway.payment.service.UserChargeService;
import br.com.nimble.gateway.payment.service.UserService;
import br.com.nimble.gateway.payment.util.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService, UserChargeService {
   
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticatedUserProvider authentication;
    private final PasswordEncoder encoder;
    private final AccountService accountService;

    @Override
    public UserResponse saveUser(UserSignupRequest userDto) {
        var userModel = UserMapper.toEntity(userDto, UserType.USER, getRoleType(Role.USER));
        validateEmailNotExists(userModel.getEmail());
        validateCpfNotExists(userModel.getCpf());
        encryptPassword(userModel);
        log.info("Registering a new User: {}", userModel.getFullName());
        return saveUser(userModel);
    }

    @Override
    public UserModel findUserByCpf(String cpf) {
        log.info("Verifying the user's CPF: {}", cpf);
        return userRepository.findByCpf(cpf).orElseThrow(() -> {
            log.error("User not found for CPF: {}", cpf);
            return new ObjectNotFoundException("User not found for CPF: " + cpf);
        });
    }

    @Override
    public TokenAndRefreshTokenResponse signin(UserSigninRequest userDto) {
		return authentication.authenticateUser(userDto);
	}
	
    @Override
	public TokenResponse refreshToken(String username, String refreshToken) {
        return tokenProvider.refreshToken(refreshToken, username);
	}

    @Transactional
    private UserResponse saveUser(UserModel userModel) {
        var user = userRepository.save(userModel);
        accountService.createAccount(user);
        return UserMapper.toDto(user);
    }

    private synchronized void validateEmailNotExists(String email) {
        log.info("Verifying the user's email: {}", email);
        var exists = userRepository.existsByEmail(email.toLowerCase());
        if (exists) {
            log.error("Email already exists: {}", email);
            throw new ValidationException("Email already exists");
        }
    }

    private synchronized void validateCpfNotExists(String cpf) {
        log.info("Verifying the user's CPF: {}", cpf);
        var exists = userRepository.existsByCpf(cpf);
        if (exists) {
            log.error("CPF already exists: {}", cpf);
            throw new ValidationException("CPF already exists");
        }
    }

    private RoleModel getRoleType(String roleName) {
        return roleService.findByRole(roleName);
    }

    private void encryptPassword(UserModel user) {
		log.info("Encrypting password");
        user.setPassword(encoder.encode(user.getPassword()));
    }
}