package br.com.nimble.gateway.payment.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.nimble.gateway.payment.domain.model.Charge;
import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.model.enums.ChargeStatus;

public interface ChargeRepository extends JpaRepository<Charge, UUID> {

    List<Charge> findByOriginator(UserModel originator);

    List<Charge> findByOriginatorAndStatus(UserModel originator, ChargeStatus status);

    List<Charge> findByRecipientAndStatus(UserModel recipient, ChargeStatus status);
}