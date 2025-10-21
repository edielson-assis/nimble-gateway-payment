package br.com.nimble.gateway.payment.domain.exception;

public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
}