package br.com.nimble.gateway.payment.service.impl;

import org.springframework.stereotype.Service;

import br.com.nimble.gateway.payment.api.v1.dto.request.CardPaymentRequest;
import br.com.nimble.gateway.payment.api.v1.mapper.CardPaymentMapper;
import br.com.nimble.gateway.payment.domain.model.CardPayment;
import br.com.nimble.gateway.payment.domain.model.Charge;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionType;
import br.com.nimble.gateway.payment.domain.repository.CardPaymentRepository;
import br.com.nimble.gateway.payment.integration.AuthorizerAdapter;
import br.com.nimble.gateway.payment.service.CardPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class CardPaymentServiceImpl implements CardPaymentService {

    private final CardPaymentRepository cardPaymentRepository;
    private final AuthorizerAdapter authorizerAdapter;

    @Override
    public CardPayment processCardPayment(CardPaymentRequest request, Charge charge) {
        var card = CardPaymentMapper.toEntity(request, charge);
        authorizerAdapter.isAuthorizedTransaction(card, charge.getAmount(), TransactionType.PAYMENT);
        log.info("Creating card payment for chargId: {}", charge.getChargeId());
        return cardPaymentRepository.save(card);
    }
}