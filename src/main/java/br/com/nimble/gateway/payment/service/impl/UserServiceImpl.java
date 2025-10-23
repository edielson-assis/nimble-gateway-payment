package br.com.nimble.gateway.payment.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.nimble.gateway.payment.api.v1.dto.request.UserPutPasswordRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.UserPutRequest;
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
import br.com.nimble.gateway.payment.domain.model.enums.UserStatus;
import br.com.nimble.gateway.payment.domain.model.enums.UserType;
import br.com.nimble.gateway.payment.domain.repository.UserRepository;
import br.com.nimble.gateway.payment.service.RoleService;
import br.com.nimble.gateway.payment.service.UserService;
import br.com.nimble.gateway.payment.util.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
   
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticatedUserProvider authentication;
    private final PasswordEncoder encoder;
    private final AccountService accountService;

    @Override
    public UserResponse saveCommonUser(UserSignupRequest userDto) {
        var userModel = validateIfUserIsModerator(userDto);
        validateEmailNotExists(userModel, userDto);
        validateCpfNotExists(userModel, userDto);
        userModel = UserMapper.toEntity(userDto, UserType.USER, getRoleType(Role.USER));
        encryptPassword(userModel);
        log.info("Registering a new User: {}", userModel.getFullName());
        return saveUser(userModel);
    }

    @Override
    public UserResponse saveModerator(UserSignupRequest userDto) {
        var userModel = validateIfUserIsModerator(userDto);
        validadeIfUserIsAdmin(userModel);
        log.info("Registering a new Moderator");
        if (!(existsByCpf(userModel, userDto) && existsByEmail(userModel, userDto))) {
            userModel = UserMapper.toEntity(userDto, UserType.MODERATOR, getRoleType(Role.MODERATOR));
            encryptPassword(userModel);
            return saveUser(userModel);
        }
        return saveUser(UserMapper.toEntity(userModel, UserType.MODERATOR, getRoleType(Role.MODERATOR)));
    }

    @Override
    public Page<UserResponse> findAllUsers(Integer page, Integer size, String direction) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
		var pageable = PageRequest.of(page, size, Sort.by(sortDirection, "userId"));
        log.info("Listing all users");
        return userRepository.findAll(pageable).map(UserMapper::toDto);
    }

    @Override
    public UserResponse findUser(UUID userId) {
        var userModel = findUserById(userId);
        return UserMapper.toDto(userModel);
    } 

    @Transactional
    @Override
    public UserResponse updateUser(UUID userId, UserPutRequest userDto) {
        var userModel = findUserById(userId);
        hasPermissionToFindUserData(userModel);
        userModel = UserMapper.toEntity(userModel, userDto);
        log.info("Updating user with name: {}", userDto.getFullName());
        userRepository.save(userModel);
        return UserMapper.toDto(userModel);
    }

    @Transactional
    @Override
    public String updateUserPassword(UserPutPasswordRequest userDto) {
        var userModel = currentUser();
        if (!encoder.matches(userDto.getOldPassword(), userModel.getPassword())) {
            log.error("Old password does not match");
            throw new ValidationException("Old password does not match");
        }
        userModel = UserMapper.toEntity(userModel, userDto);
        encryptPassword(userModel);
        log.info("Updating password");
        userRepository.save(userModel);
        return "Password updated successfully";
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
    @Override
    public void disableUser(UUID userId) {
        var userModel = findUserById(userId);
        hasPermissionToFindUserData(userModel);
		log.info("Disabling user with email: {}", userModel.getEmail());
		userModel.setEnabled(false);
        userModel.setUserStatus(UserStatus.INACTIVE);
		userRepository.save(userModel);
	}

    @Transactional
    private UserResponse saveUser(UserModel userModel) {
        var user = userRepository.save(userModel);
        accountService.createForUser(user);
        return UserMapper.toDto(user);
    }

    private UserModel validateIfUserIsModerator(UserSignupRequest user) {
        var userModel = findByEmailOrCpf(user.getEmail(), user.getCpf());
        if (userModel != null && userModel.getUserType() == UserType.MODERATOR) {
            log.error("User is already a moderator: {}", user.getEmail());
            throw new ValidationException("User is already a moderator");
        }
        return userModel;
    }

    private void validadeIfUserIsAdmin(UserModel userModel) {
        if (userModel != null && userModel.getUserType() == UserType.ADMIN) {
            log.error("Cannot modify an admin user: {}", userModel.getEmail());
            throw new ValidationException("Cannot modify an admin user");
        }
    }

    private void validateEmailNotExists(UserModel userModel, UserSignupRequest userDto) {
        if (existsByEmail(userModel, userDto)) {
            log.error("Email already exists: {}", userModel.getEmail());
            throw new ValidationException("Email already exists");
        }
    }

    private void validateCpfNotExists(UserModel userModel, UserSignupRequest userDto) {
        if (existsByCpf(userModel, userDto)) {
            log.error("CPF already exists: {}", userModel.getCpf());
            throw new ValidationException("CPF already exists");
        }
    }

    private boolean existsByEmail(UserModel userModel, UserSignupRequest userDto) {
        return userModel != null && userModel.getEmail().equals(userDto.getEmail());
    }

    private boolean existsByCpf(UserModel userModel, UserSignupRequest userDto) {
        return userModel != null && userModel.getCpf().equals(userDto.getCpf());
    }

    private RoleModel getRoleType(String roleName) {
        return roleService.findByRole(roleName);
    }

    private void encryptPassword(UserModel user) {
		log.info("Encrypting password");
        user.setPassword(encoder.encode(user.getPassword()));
    }

    private UserModel findUserById(UUID userId) {
        log.info("Verifying the user's Id: {}", userId);
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("User id not found: {}", userId);
            return new ObjectNotFoundException("User id not found: " + userId);
        }); 
    }

    private UserModel findByEmailOrCpf(String email, String cpf) {
        log.info("Verifying the user's email or CPF");
        return userRepository.findByEmailOrCpf(email, cpf).orElse(null);
    }

    private boolean isAdminOrModerator(UserModel user) {
        return user.getAuthorities().stream().anyMatch(permission -> 
                permission.getAuthority().equals("ROLE_ADMIN") ||
                permission.getAuthority().equals("ROLE_MODERATOR"));
    }

    private UserModel currentUser() {
        return authentication.getCurrentUser();
    }

    private void hasPermissionToFindUserData(UserModel userModel) {
        UserModel loggedUser = currentUser();
        if (!(loggedUser.equals(userModel) || isAdminOrModerator(loggedUser))) {
            log.error("Permission denied");
            throw new AccessDeniedException("Permission denied");
        }
    }
}