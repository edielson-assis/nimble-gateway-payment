CREATE TABLE charges(
    charge_id UUID,
    amount NUMERIC(19,2) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20),
    originator_id UUID NOT NULL,
    recipient_id UUID NOT NULL,
    paid_at TIMESTAMP,
    canceled_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_charges PRIMARY KEY(charge_id),
    CONSTRAINT fk_charges_originator FOREIGN KEY(originator_id) REFERENCES users(user_id),
    CONSTRAINT fk_charges_recipient FOREIGN KEY(recipient_id) REFERENCES users(user_id)
);