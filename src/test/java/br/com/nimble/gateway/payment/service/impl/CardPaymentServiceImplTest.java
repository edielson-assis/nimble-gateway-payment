package br.com.nimble.gateway.payment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.nimble.gateway.payment.api.v1.dto.request.CardPaymentRequest;
import br.com.nimble.gateway.payment.domain.model.CardPayment;
import br.com.nimble.gateway.payment.domain.model.Charge;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionType;
import br.com.nimble.gateway.payment.domain.repository.CardPaymentRepository;
import br.com.nimble.gateway.payment.integration.AuthorizerAdapter;

@ExtendWith(MockitoExtension.class)
class CardPaymentServiceImplTest {

    @Mock
    private CardPaymentRepository cardPaymentRepository;
    @Mock
    private AuthorizerAdapter authorizerAdapter;

    @InjectMocks
    private CardPaymentServiceImpl service;

    private CardPaymentRequest buildRequest() {
        CardPaymentRequest request = new CardPaymentRequest();
        request.setCardNumber("4111111111111111");
        request.setCardHolder("John Doe");
        request.setCvv("123");
        request.setExpirationDate("12/26");
        return request;
    }

    private Charge buildCharge(BigDecimal amount) {
        Charge charge = new Charge();
        charge.setChargeId(UUID.randomUUID());
        charge.setAmount(amount);
        return charge;
    }

    @Test
    @DisplayName("Should map request to entity, authorize, and persist card payment")
    void shouldProcessCardPaymentAndPersist() {
        // Arrange
        CardPaymentRequest request = buildRequest();
        Charge charge = buildCharge(new BigDecimal("50.00"));
        when(cardPaymentRepository.save(any(CardPayment.class))).thenAnswer(inv -> {
            CardPayment card = inv.getArgument(0);
            card.setCardPaymentId(UUID.randomUUID());
            return card;
        });

        // Act
        CardPayment result = service.processCardPayment(request, charge);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getCardPaymentId());
        assertEquals(charge.getChargeId(), result.getCharge().getChargeId());
        verify(authorizerAdapter).isAuthorizedTransaction(any(CardPayment.class), eq(charge.getAmount()), eq(TransactionType.PAYMENT));
        verify(cardPaymentRepository).save(any(CardPayment.class));
    }

    @Test
    @DisplayName("Should pass exact charge amount to authorizer")
    void shouldPassExactAmountToAuthorizer() {
        // Arrange
        CardPaymentRequest request = buildRequest();
        Charge charge = buildCharge(new BigDecimal("123.45"));
        when(cardPaymentRepository.save(any(CardPayment.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        service.processCardPayment(request, charge);

        // Assert
        verify(authorizerAdapter).isAuthorizedTransaction(any(CardPayment.class), eq(new BigDecimal("123.45")), eq(TransactionType.PAYMENT));
    }

    @Test
    @DisplayName("Should use mapper to include charge reference in entity")
    void shouldIncludeChargeReference() {
        // Arrange
        CardPaymentRequest request = buildRequest();
        Charge charge = buildCharge(new BigDecimal("77.00"));
        when(cardPaymentRepository.save(any(CardPayment.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        CardPayment saved = service.processCardPayment(request, charge);

        // Assert
        assertEquals(charge.getChargeId(), saved.getCharge().getChargeId());
    }

    @Test
    @DisplayName("Should invoke repository save exactly once")
    void shouldInvokeRepositorySaveOnce() {
        // Arrange
        CardPaymentRequest request = buildRequest();
        Charge charge = buildCharge(new BigDecimal("10.00"));
        when(cardPaymentRepository.save(any(CardPayment.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        service.processCardPayment(request, charge);

        // Assert
        verify(cardPaymentRepository).save(any(CardPayment.class));
    }

    @Test
    @DisplayName("Should authorize with TransactionType.PAYMENT")
    void shouldAuthorizeWithPaymentType() {
        // Arrange
        CardPaymentRequest request = buildRequest();
        Charge charge = buildCharge(new BigDecimal("10.00"));
        when(cardPaymentRepository.save(any(CardPayment.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        service.processCardPayment(request, charge);

        // Assert
        verify(authorizerAdapter).isAuthorizedTransaction(any(CardPayment.class), eq(new BigDecimal("10.00")), eq(TransactionType.PAYMENT));
    }
}