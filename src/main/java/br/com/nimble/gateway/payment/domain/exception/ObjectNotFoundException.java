package br.com.nimble.gateway.payment.domain.exception;

public class ObjectNotFoundException extends RuntimeException {
    
    public ObjectNotFoundException(String message) {
        super(message);
    }
}