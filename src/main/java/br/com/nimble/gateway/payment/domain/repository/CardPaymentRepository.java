package br.com.nimble.gateway.payment.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.nimble.gateway.payment.domain.model.CardPayment;

public interface CardPaymentRepository extends JpaRepository<CardPayment, UUID> {}