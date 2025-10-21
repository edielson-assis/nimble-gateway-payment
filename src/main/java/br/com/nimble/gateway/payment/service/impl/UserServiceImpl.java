package br.com.nimble.gateway.payment.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.nimble.gateway.payment.api.v1.dto.request.UserSignupRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.UserResponse;
import br.com.nimble.gateway.payment.api.v1.mapper.UserMapper;
import br.com.nimble.gateway.payment.domain.exception.ObjectNotFoundException;
import br.com.nimble.gateway.payment.domain.model.RoleModel;
import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.model.enums.UserStatus;
import br.com.nimble.gateway.payment.domain.model.enums.UserType;
import br.com.nimble.gateway.payment.domain.repository.UserRepository;
import br.com.nimble.gateway.payment.service.RoleService;
import br.com.nimble.gateway.payment.service.UserService;
import br.com.nimble.gateway.payment.util.Role;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
   
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder encoder;

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
        log.info("Registering a new Moderator: {}", userModel.getFullName());
        if (!existsByCpf(userModel, userDto) && !existsByEmail(userModel, userDto)) {
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
    public void disableUser(UUID userId) {
        var userModel = findUserById(userId);
		log.info("Disabling user with email: {}", userModel.getEmail());
		userModel.setEnabled(false);
        userModel.setUserStatus(UserStatus.INACTIVE);
		userRepository.save(userModel);
	}

    @Transactional
    private UserResponse saveUser(UserModel userModel) {
        userRepository.save(userModel);
        return UserMapper.toDto(userModel);
    }

    private UserModel validateIfUserIsModerator(UserSignupRequest user) {
        var userModel = findByEmailOrCpf(user.getEmail(), user.getCpf());
        if (userModel != null && userModel.getUserType() == UserType.MODERATOR) {
            log.error("User is already a moderator: {}", user.getEmail());
            throw new ValidationException("User is already a moderator");
        }
        return userModel;
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
}