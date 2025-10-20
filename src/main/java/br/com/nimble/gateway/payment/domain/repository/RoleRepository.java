package br.com.nimble.gateway.payment.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.nimble.gateway.payment.domain.model.RoleModel;

public interface RoleRepository extends JpaRepository<RoleModel, UUID> {
    
    Optional<RoleModel> findByRoleName(String name);
}