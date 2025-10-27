package br.com.nimble.gateway.payment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.nimble.gateway.payment.api.v1.dto.request.UserSigninRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.UserSignupRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.TokenAndRefreshTokenResponse;
import br.com.nimble.gateway.payment.api.v1.dto.response.TokenResponse;
import br.com.nimble.gateway.payment.api.v1.dto.response.UserResponse;
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
import br.com.nimble.gateway.payment.util.Role;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuthenticatedUserProvider authentication;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<UserModel> userModelCaptor;

    private UserSignupRequest userSignupRequest;
    private UserSigninRequest userSigninRequest;
    private UserModel userModel;
    private RoleModel roleModel;
    private final String cpf = "12345678900";
    private final String email = "test@example.com";
    private final String password = "password123";
    private final String encodedPassword = "encodedPassword123";
    private final String fullName = "Test User";

    @BeforeEach
    void setUp() {
        // Setup UserSignupRequest
        userSignupRequest = new UserSignupRequest();
        userSignupRequest.setCpf(cpf);
        userSignupRequest.setEmail(email);
        userSignupRequest.setPassword(password);
        userSignupRequest.setFullName(fullName);

        // Setup UserSigninRequest
        userSigninRequest = new UserSigninRequest();

        // Setup RoleModel
        roleModel = new RoleModel();
        roleModel.setRoleId(UUID.randomUUID());
        roleModel.setRoleName(Role.USER);

        // Setup UserModel
        userModel = new UserModel();
        userModel.setUserId(UUID.randomUUID());
        userModel.setCpf(cpf);
        userModel.setEmail(email);
        userModel.setPassword(password);
        userModel.setFullName(fullName);
        userModel.setUserType(UserType.USER);
        userModel.getPermissions().add(roleModel);
    }

    @Test
    @DisplayName("Should save user successfully")
    void saveUser_success() {
        // Arrange
        when(roleService.findByRole(Role.USER)).thenReturn(roleModel);
        when(userRepository.existsByEmail(email.toLowerCase())).thenReturn(false);
        when(userRepository.existsByCpf(cpf)).thenReturn(false);
        when(encoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);

        // Act
        UserResponse result = userService.saveUser(userSignupRequest);

        // Assert
        assertNotNull(result);
        assertEquals(fullName, result.getFullName());
        assertEquals(email, result.getEmail());
        assertEquals(cpf, result.getCpf());
        verify(roleService).findByRole(Role.USER);
        verify(userRepository).existsByEmail(email.toLowerCase());
        verify(userRepository).existsByCpf(cpf);
        verify(encoder).encode(password);
        verify(userRepository).save(userModelCaptor.capture());
        verify(accountService).createAccount(userModel);

        UserModel capturedUser = userModelCaptor.getValue();

        assertEquals(email, capturedUser.getEmail());
        assertEquals(cpf, capturedUser.getCpf());
        assertEquals(encodedPassword, capturedUser.getPassword());
        assertEquals(fullName, capturedUser.getFullName());
        assertEquals(UserType.USER, capturedUser.getUserType());
    }

    @Test
    @DisplayName("Should throw ValidationException when email already exists")
    void saveUser_emailAlreadyExists() {
        // Arrange
        when(roleService.findByRole(Role.USER)).thenReturn(roleModel);
        when(userRepository.existsByEmail(email.toLowerCase())).thenReturn(true);

        // Act & Assert
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> userService.saveUser(userSignupRequest)
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(roleService).findByRole(Role.USER);
        verify(userRepository).existsByEmail(email.toLowerCase());
        verify(userRepository, never()).existsByCpf(anyString());
        verify(encoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserModel.class));
        verify(accountService, never()).createAccount(any(UserModel.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when CPF already exists")
    void saveUser_cpfAlreadyExists() {
        // Arrange
        when(roleService.findByRole(Role.USER)).thenReturn(roleModel);
        when(userRepository.existsByEmail(email.toLowerCase())).thenReturn(false);
        when(userRepository.existsByCpf(cpf)).thenReturn(true);

        // Act & Assert
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> userService.saveUser(userSignupRequest)
        );

        assertEquals("CPF already exists", exception.getMessage());
        verify(roleService).findByRole(Role.USER);
        verify(userRepository).existsByEmail(email.toLowerCase());
        verify(userRepository).existsByCpf(cpf);
        verify(encoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserModel.class));
        verify(accountService, never()).createAccount(any(UserModel.class));
    }

    @Test
    @DisplayName("Should find user by CPF successfully")
    void findUserByCpf_success() {
        // Arrange
        when(userRepository.findByCpf(cpf)).thenReturn(Optional.of(userModel));

        // Act
        UserModel result = userService.findUserByCpf(cpf);

        // Assert
        assertNotNull(result);
        assertEquals(userModel.getUserId(), result.getUserId());
        assertEquals(cpf, result.getCpf());
        assertEquals(email, result.getEmail());
        assertEquals(fullName, result.getFullName());
        verify(userRepository).findByCpf(cpf);
    }

    @Test
    @DisplayName("Should throw ObjectNotFoundException when user not found by CPF")
    void findUserByCpf_notFound() {
        // Arrange
        String nonExistentCpf = "98765432100";
        when(userRepository.findByCpf(nonExistentCpf)).thenReturn(Optional.empty());

        // Act & Assert
        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> userService.findUserByCpf(nonExistentCpf)
        );

        assertEquals("User not found for CPF: " + nonExistentCpf, exception.getMessage());
        verify(userRepository).findByCpf(nonExistentCpf);
    }

    @Test
    @DisplayName("Should sign in user successfully")
    void signin_success() {
        // Arrange
        TokenAndRefreshTokenResponse expectedResponse = new TokenAndRefreshTokenResponse(userSigninRequest.getEmailOrCpf(), userSigninRequest.getPassword());
        expectedResponse.accessToken();
        expectedResponse.refreshToken();
        
        when(authentication.authenticateUser(userSigninRequest)).thenReturn(expectedResponse);

        // Act
        TokenAndRefreshTokenResponse result = userService.signin(userSigninRequest);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse.accessToken(), result.accessToken());
        assertEquals(expectedResponse.refreshToken(), result.refreshToken());
        verify(authentication).authenticateUser(userSigninRequest);
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void refreshToken_success() {
        // Arrange
        String username = email;
        String refreshToken = "refresh-token";
        TokenResponse expectedResponse = new TokenResponse("new-jwt-token");
        expectedResponse.accessToken();
        
        when(tokenProvider.refreshToken(refreshToken, username)).thenReturn(expectedResponse);

        // Act
        TokenResponse result = userService.refreshToken(username, refreshToken);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse.accessToken(), result.accessToken());
        verify(tokenProvider).refreshToken(refreshToken, username);
    }
}