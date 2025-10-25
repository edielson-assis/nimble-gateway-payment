package br.com.nimble.gateway.payment.service.impl;

import org.springframework.stereotype.Service;

import br.com.nimble.gateway.payment.api.v1.dto.request.CardPaymentRequest;
import br.com.nimble.gateway.payment.domain.repository.CardPaymentRepository;
import br.com.nimble.gateway.payment.service.CardPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class CardPaymentServiceImpl implements CardPaymentService {

    private final CardPaymentRepository cardPaymentRepository;

    @Override
    public String processCardPayment(CardPaymentRequest request) {
        // Implement the logic to process the card payment here
        return "Payment processed successfully";
    }
}