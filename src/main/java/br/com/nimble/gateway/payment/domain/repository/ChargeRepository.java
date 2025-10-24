package br.com.nimble.gateway.payment.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.nimble.gateway.payment.domain.model.Charge;
import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.model.enums.ChargeStatus;

public interface ChargeRepository extends JpaRepository<Charge, UUID> {

    @Query("SELECT c FROM Charge c WHERE c.originator = :originator")
    Page<Charge> findByOriginator(@Param("originator") UserModel originator, Pageable pageable);

    @Query("SELECT c FROM Charge c WHERE c.recipient = :recipient")
	Page<Charge> findByRecipient(@Param("recipient") UserModel recipient, Pageable pageable);

    @Query("SELECT c FROM Charge c WHERE c.originator = :originator AND c.status = :status")
    Page<Charge> findByOriginatorAndStatus(@Param("originator") UserModel originator, @Param("status") ChargeStatus status, Pageable pageable);

    @Query("SELECT c FROM Charge c WHERE c.recipient = :recipient AND c.status = :status")
    Page<Charge> findByRecipientAndStatus(@Param("recipient") UserModel recipient, @Param("status") ChargeStatus status, Pageable pageable);

    @Query("SELECT c FROM Charge c WHERE c.chargeId = :chargeId AND c.recipient.userId = :recipientId")
    Optional<Charge> findByChargeIdAndRecipientId(@Param("chargeId") UUID chargeId, @Param("recipientId") UUID recipientId);
}