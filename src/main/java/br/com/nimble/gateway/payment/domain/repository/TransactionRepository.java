package br.com.nimble.gateway.payment.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.nimble.gateway.payment.domain.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {}