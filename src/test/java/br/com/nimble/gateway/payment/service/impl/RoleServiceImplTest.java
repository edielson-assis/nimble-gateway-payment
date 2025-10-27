package br.com.nimble.gateway.payment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.nimble.gateway.payment.domain.exception.ObjectNotFoundException;
import br.com.nimble.gateway.payment.domain.model.RoleModel;
import br.com.nimble.gateway.payment.domain.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository repository;

    @InjectMocks
    private RoleServiceImpl service;

    private RoleModel roleModel;
    private final String roleName = "ROLE_USER";

    @BeforeEach
    void setUp() {
        roleModel = new RoleModel();
        roleModel.setRoleId(UUID.randomUUID());
        roleModel.setRoleName(roleName);
    }

    @Test
    @DisplayName("Should find role by name successfully")
    void findByRole_success() {
        // Arrange
        when(repository.findByRoleName(roleName)).thenReturn(Optional.of(roleModel));

        // Act
        RoleModel result = service.findByRole(roleName);

        // Assert
        assertNotNull(result);
        assertEquals(roleModel.getRoleId(), result.getRoleId());
        assertEquals(roleName, result.getRoleName());
        verify(repository).findByRoleName(roleName);
    }

    @Test
    @DisplayName("Should throw ObjectNotFoundException when role not found")
    void findByRole_notFound() {
        // Arrange
        String nonExistentRole = "ROLE_NONEXISTENT";
        when(repository.findByRoleName(nonExistentRole)).thenReturn(Optional.empty());

        // Act & Assert
        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> service.findByRole(nonExistentRole)
        );

        assertEquals("Role not found", exception.getMessage());
        verify(repository).findByRoleName(nonExistentRole);
    }

    @Test
    @DisplayName("Should handle null role name")
    void findByRole_nullRoleName() {
        // Arrange
        String nullRoleName = null;
        when(repository.findByRoleName(nullRoleName)).thenReturn(Optional.empty());

        // Act & Assert
        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> service.findByRole(nullRoleName)
        );

        assertEquals("Role not found", exception.getMessage());
        verify(repository).findByRoleName(nullRoleName);
    }

    @Test
    @DisplayName("Should handle empty role name")
    void findByRole_emptyRoleName() {
        // Arrange
        String emptyRoleName = "";
        when(repository.findByRoleName(emptyRoleName)).thenReturn(Optional.empty());

        // Act & Assert
        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> service.findByRole(emptyRoleName)
        );

        assertEquals("Role not found", exception.getMessage());
        verify(repository).findByRoleName(emptyRoleName);
    }
}