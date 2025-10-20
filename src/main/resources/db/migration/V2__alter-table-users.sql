ALTER TABLE users 
ADD COLUMN is_account_non_expired BOOLEAN NOT NULL,
ADD COLUMN is_account_non_locked BOOLEAN NOT NULL,
ADD COLUMN is_credentials_non_expired BOOLEAN NOT NULL,
ADD COLUMN is_enabled BOOLEAN NOT NULL;