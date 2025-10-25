package br.com.nimble.gateway.payment.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.nimble.gateway.payment.domain.model.UserModel;

public interface UserRepository extends JpaRepository<UserModel, UUID>, JpaSpecificationExecutor<UserModel> {

    @EntityGraph(attributePaths = "permissions", type = EntityGraph.EntityGraphType.FETCH)
    Optional<UserModel> findByEmailOrCpf(String email, String cpf);

    Optional<UserModel> findByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}