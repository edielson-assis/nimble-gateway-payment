CREATE TABLE card_payments(
    card_payment_id UUID,
    charge_id UUID NOT NULL,
    card_last4 CHAR(4),
    authorizer_status VARCHAR(50),
    authorizer_message VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_card_payments PRIMARY KEY(card_payment_id),
    CONSTRAINT fk_card_payments_charge FOREIGN KEY(charge_id) REFERENCES charges(charge_id),
    CONSTRAINT uq_card_payments_charge UNIQUE(charge_id)
);