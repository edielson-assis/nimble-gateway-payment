package br.com.nimble.gateway.payment.service;

import br.com.nimble.gateway.payment.api.v1.dto.request.CardPaymentRequest;

public interface CardPaymentService {
    
    String processCardPayment(CardPaymentRequest request);
}