package br.com.nimble.gateway.payment.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(of = "cardPaymentId")
@Getter
@Setter
@Entity
@Table(name = "card_payments")
public class CardPayment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID cardPaymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_id", nullable = false, unique = true)
    private Charge charge;

    @Column(name = "card_holder_name", length = 100)
    private String cardHolderName;

    @Column(name = "card_last4", length = 4)
    private String cardLast4;

    @Column(name = "card_expiration", length = 5)
    private String cardExpiration;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();
}