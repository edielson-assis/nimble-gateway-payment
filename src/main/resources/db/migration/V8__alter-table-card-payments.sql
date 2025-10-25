ALTER TABLE card_payments
DROP COLUMN authorizer_status,
DROP COLUMN authorizer_message;

ALTER TABLE card_payments
ADD COLUMN card_holder_name VARCHAR(50),
ADD COLUMN card_expiration VARCHAR(5);