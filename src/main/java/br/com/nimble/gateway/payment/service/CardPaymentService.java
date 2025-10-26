package br.com.nimble.gateway.payment.service;

import br.com.nimble.gateway.payment.api.v1.dto.request.CardPaymentRequest;
import br.com.nimble.gateway.payment.domain.model.CardPayment;
import br.com.nimble.gateway.payment.domain.model.Charge;

public interface CardPaymentService {
    
    CardPayment processCardPayment(CardPaymentRequest request, Charge charge);
}