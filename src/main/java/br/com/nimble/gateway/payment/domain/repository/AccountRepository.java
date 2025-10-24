package br.com.nimble.gateway.payment.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.nimble.gateway.payment.domain.model.Account;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query("SELECT a FROM Account a WHERE a.user.userId = :userId")
    Optional<Account> findByUserId(@Param("userId") UUID userId);
}