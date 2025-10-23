CREATE TABLE transactions(
    transaction_id UUID,
    type VARCHAR(20) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    from_account_id UUID,
    to_account_id UUID,
    charge_id UUID,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transactions PRIMARY KEY(transaction_id),
    CONSTRAINT fk_tx_from_account FOREIGN KEY(from_account_id) REFERENCES accounts(account_id),
    CONSTRAINT fk_tx_to_account FOREIGN KEY(to_account_id) REFERENCES accounts(account_id),
    CONSTRAINT fk_tx_charge FOREIGN KEY(charge_id) REFERENCES charges(charge_id)
);