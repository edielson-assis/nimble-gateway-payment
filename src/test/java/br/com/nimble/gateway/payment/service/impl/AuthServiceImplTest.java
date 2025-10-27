package br.com.nimble.gateway.payment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Should return UserDetails when user exists by email")
    void shouldReturnUserDetailsWhenUserExistsByEmail() {
        // Arrange
        UserModel user = new UserModel();
        when(userRepository.findByEmailOrCpf("user@example.com", "user@example.com"))
            .thenReturn(Optional.of(user));

        // Act
        UserDetails result = authService.loadUserByUsername("user@example.com");

        // Assert
        assertNotNull(result);
        verify(userRepository).findByEmailOrCpf("user@example.com", "user@example.com");
    }

    @Test
    @DisplayName("Should return UserDetails when user exists by CPF")
    void shouldReturnUserDetailsWhenUserExistsByCpf() {
        // Arrange
        UserModel user = new UserModel();
        when(userRepository.findByEmailOrCpf("12345678901", "12345678901"))
            .thenReturn(Optional.of(user));

        // Act
        UserDetails result = authService.loadUserByUsername("12345678901");

        // Assert
        assertNotNull(result);
        verify(userRepository).findByEmailOrCpf("12345678901", "12345678901");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void shouldThrowWhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmailOrCpf(anyString(), anyString()))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> authService.loadUserByUsername("unknown@example.com"));
        verify(userRepository).findByEmailOrCpf("unknown@example.com", "unknown@example.com");
    }

    @Test
    @DisplayName("Should pass the same parameter as both email and CPF to repository")
    void shouldPassSameParameterForEmailAndCpf() {
        // Arrange
        String input = "inputValue";
        UserModel user = new UserModel();
        when(userRepository.findByEmailOrCpf(input, input)).thenReturn(Optional.of(user));

        // Act
        authService.loadUserByUsername(input);

        // Assert
        verify(userRepository).findByEmailOrCpf(input, input);
    }

    @Test
    @DisplayName("Should propagate returned UserDetails without modification")
    void shouldReturnExactUserDetailsFromRepository() {
        // Arrange
        UserModel user = new UserModel();
        when(userRepository.findByEmailOrCpf("a@b.com", "a@b.com"))
            .thenReturn(Optional.of(user));

        // Act
        UserDetails result = authService.loadUserByUsername("a@b.com");

        // Assert
        assertEquals(user, result);
    }
}