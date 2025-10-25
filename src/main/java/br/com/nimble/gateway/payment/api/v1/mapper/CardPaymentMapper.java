package br.com.nimble.gateway.payment.api.v1.mapper;

import br.com.nimble.gateway.payment.api.v1.dto.request.CardPaymentRequest;
import br.com.nimble.gateway.payment.domain.model.CardPayment;
import br.com.nimble.gateway.payment.domain.model.Charge;

public class CardPaymentMapper {
    
    private CardPaymentMapper() {}

    public static CardPayment toEntity(CardPaymentRequest request, Charge charge) {
        var cardPayment = new CardPayment();
        cardPayment.setCardHolderName(request.getCardHolder());
        cardPayment.setCardLast4(cardLast4(request));
        cardPayment.setCardExpiration(request.getExpirationDate());
        cardPayment.setCharge(charge);
        return cardPayment;
    }

    private static String cardLast4(CardPaymentRequest request) {
        return request.getCardNumber().substring(request.getCardNumber().length() - 4);
    }
}