CREATE TABLE accounts(
    account_id UUID,
    balance NUMERIC(19,2) NOT NULL DEFAULT 0.00,
    user_id UUID NOT NULL,

    CONSTRAINT pk_accounts PRIMARY KEY(account_id),
    CONSTRAINT fk_accounts_user FOREIGN KEY(user_id) REFERENCES users(user_id),
    CONSTRAINT uq_accounts_user UNIQUE(user_id)
);