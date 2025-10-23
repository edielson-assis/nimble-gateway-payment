package br.com.nimble.gateway.payment.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.nimble.gateway.payment.domain.model.Account;

public interface AccountRepository extends JpaRepository<Account, UUID> {}