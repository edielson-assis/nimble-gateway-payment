package br.com.nimble.gateway.payment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.nimble.gateway.payment.api.v1.dto.request.CardPaymentRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.ChargeRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.ChargeResponse;
import br.com.nimble.gateway.payment.config.security.context.AuthenticatedUserProvider;
import br.com.nimble.gateway.payment.domain.exception.ObjectNotFoundException;
import br.com.nimble.gateway.payment.domain.exception.ValidationException;
import br.com.nimble.gateway.payment.domain.model.Account;
import br.com.nimble.gateway.payment.domain.model.CardPayment;
import br.com.nimble.gateway.payment.domain.model.Charge;
import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.model.enums.ChargeStatus;
import br.com.nimble.gateway.payment.domain.model.enums.PaymentMethod;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionStatus;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionType;
import br.com.nimble.gateway.payment.domain.repository.ChargeRepository;
import br.com.nimble.gateway.payment.integration.AuthorizerAdapter;
import br.com.nimble.gateway.payment.service.AccountChargeService;
import br.com.nimble.gateway.payment.service.AccountService;
import br.com.nimble.gateway.payment.service.CardPaymentService;
import br.com.nimble.gateway.payment.service.TransactionService;
import br.com.nimble.gateway.payment.service.UserChargeService;

@ExtendWith(MockitoExtension.class)
class ChargeServiceImplTest {

    @Mock private ChargeRepository chargeRepository;
    @Mock private AuthenticatedUserProvider authentication;
    @Mock private UserChargeService userService;
    @Mock private AccountService accountService;
    @Mock private AccountChargeService accountChargeService;
    @Mock private CardPaymentService cardPaymentService;
    @Mock private AuthorizerAdapter authorizerAdapter;
    @Mock private TransactionService transactionService;

    @InjectMocks private ChargeServiceimpl service;

    private UserModel user(String cpf) {
        UserModel user = new UserModel();
        user.setUserId(UUID.randomUUID());
        user.setCpf(cpf);
        return user;
    }

    private Charge pendingCharge(UserModel originator, UserModel recipient, BigDecimal amount) {
        Charge charge = new Charge();
        charge.setChargeId(UUID.randomUUID());
        charge.setOriginator(originator);
        charge.setRecipient(recipient);
        charge.setAmount(amount);
        charge.setStatus(ChargeStatus.PENDING);
        return charge;
    }

    @BeforeEach
    void setup() {
        // default current user
        when(authentication.getCurrentUser()).thenReturn(user("11122233344"));
    }

    @Test
    @DisplayName("Should create charge and set status PENDING")
    void createCharge_setsPendingAndPersists() {
        // Arrange
        ChargeRequest req = new ChargeRequest();
        req.setRecipientCpf("22233344455");
        req.setAmount(new BigDecimal("10.00"));

        UserModel current = user("11122233344");
        UserModel recipient = user("22233344455");
        when(authentication.getCurrentUser()).thenReturn(current);
        when(userService.findUserByCpf("22233344455")).thenReturn(recipient);

        // Act
        ChargeResponse resp = service.createCharge(req);

        // Assert
        assertNotNull(resp);
        assertEquals(ChargeStatus.PENDING, resp.getChargeStatus());
        verify(chargeRepository).save(any(Charge.class));
    }

    @Test
    @DisplayName("Should throw when originator equals recipient")
    void createCharge_originatorEqualsRecipient_throws() {
        // Arrange
        ChargeRequest req = new ChargeRequest();
        req.setRecipientCpf("11122233344");
        req.setAmount(new BigDecimal("5.00"));

        UserModel current = user("11122233344");
        when(authentication.getCurrentUser()).thenReturn(current);
        when(userService.findUserByCpf("11122233344")).thenReturn(current);

        // Act & Assert
        assertThrows(ValidationException.class, () -> service.createCharge(req));
        verify(chargeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should pay with balance and register transaction")
    void paidWithBalance_happyPath() {
        // Arrange
        UserModel recipient = user("99988877766");
        UserModel originator = user("55566677788");
        when(authentication.getCurrentUser()).thenReturn(recipient);
        when(userService.findUserByCpf(recipient.getCpf())).thenReturn(recipient);

        Charge charge = pendingCharge(originator, recipient, new BigDecimal("30.00"));
        when(chargeRepository.findByChargeIdAndRecipientId(eq(charge.getChargeId()), eq(recipient.getUserId())))
            .thenReturn(Optional.of(charge));

        Account originatorAccount = new Account(); originatorAccount.setAccountId(UUID.randomUUID());
        Account recipientAccount = new Account(); recipientAccount.setAccountId(UUID.randomUUID());
        when(accountChargeService.findAccountById(originator.getUserId())).thenReturn(originatorAccount);
        when(accountChargeService.findAccountById(recipient.getUserId())).thenReturn(recipientAccount);

        // Act
        ChargeResponse resp = service.paidWithBalance(charge.getChargeId());

        // Assert
        assertEquals(ChargeStatus.PAID, resp.getChargeStatus());
        verify(accountService).payWithBalance(new BigDecimal("30.00"));
        verify(accountChargeService).creditBalance(eq(originator.getUserId()), eq(new BigDecimal("30.00")));
        verify(transactionService).registerBalancePaid(eq(new BigDecimal("30.00")), eq(recipientAccount), eq(originatorAccount), eq(charge), eq(TransactionType.PAYMENT), eq(TransactionStatus.SUCCESS));
        verify(chargeRepository).save(any(Charge.class));
    }

    @Test
    @DisplayName("Should pay with card and persist card payment")
    void paidWithCard_happyPath() {
        // Arrange
        UserModel recipient = user("44455566677");
        UserModel originator = user("11122233344");
        when(authentication.getCurrentUser()).thenReturn(recipient);
        when(userService.findUserByCpf(recipient.getCpf())).thenReturn(recipient);

        Charge charge = pendingCharge(originator, recipient, new BigDecimal("80.00"));
        when(chargeRepository.findByChargeIdAndRecipientId(eq(charge.getChargeId()), eq(recipient.getUserId())))
            .thenReturn(Optional.of(charge));

        CardPaymentRequest cardReq = new CardPaymentRequest();
        cardReq.setCardHolder("John");
        CardPayment cardPayment = new CardPayment();
        when(cardPaymentService.processCardPayment(eq(cardReq), eq(charge))).thenReturn(cardPayment);

        // Act
        service.paidWithCard(charge.getChargeId(), cardReq);

        // Assert
        verify(transactionService).registerCardPaid(eq(new BigDecimal("80.00")), eq(charge), eq(cardPayment), eq(TransactionType.PAYMENT), eq(TransactionStatus.SUCCESS));
        verify(chargeRepository).save(any(Charge.class));
    }

    @Test
    @DisplayName("Should cancel paid card charge performing refund and validations")
    void cancelCardCharge_refundPaid() {
        // Arrange
        UserModel originator = user("12345678901");
        UserModel recipient = user("98765432100");
        when(authentication.getCurrentUser()).thenReturn(originator);
        when(userService.findUserByCpf(originator.getCpf())).thenReturn(originator);

        Charge charge = pendingCharge(originator, recipient, new BigDecimal("15.00"));
        charge.setStatus(ChargeStatus.PAID);
        charge.setPaymentMethod(PaymentMethod.CARD);

        when(chargeRepository.findByChargeIdAndOriginatortId(eq(charge.getChargeId()), eq(originator.getUserId())))
            .thenReturn(Optional.of(charge));

        // Act
        ChargeResponse resp = service.cancelCardCharge(charge.getChargeId());

        // Assert
        assertEquals(ChargeStatus.CANCELED, resp.getChargeStatus());
        verify(authorizerAdapter).isAuthorizedTransaction(eq(charge.getChargeId()), eq(new BigDecimal("15.00")), eq(TransactionType.REFUND));
        verify(transactionService).registerCardRefund(eq(new BigDecimal("15.00")), eq(charge), eq(TransactionType.REFUND), eq(TransactionStatus.SUCCESS));
        verify(chargeRepository).save(any(Charge.class));
    }

    @Test
    @DisplayName("Should throw when canceling with wrong method")
    void cancelBalanceCharge_wrongMethod_throws() {
        // Arrange
        UserModel originator = user("12345678901");
        UserModel recipient = user("98765432100");
        when(authentication.getCurrentUser()).thenReturn(originator);
        when(userService.findUserByCpf(originator.getCpf())).thenReturn(originator);

        Charge charge = pendingCharge(originator, recipient, new BigDecimal("20.00"));
        charge.setStatus(ChargeStatus.PAID);
        charge.setPaymentMethod(PaymentMethod.CARD);

        when(chargeRepository.findByChargeIdAndOriginatortId(eq(charge.getChargeId()), eq(originator.getUserId())))
            .thenReturn(Optional.of(charge));

        // Act & Assert
        ValidationException ex = assertThrows(ValidationException.class, () -> service.cancelBalanceCharge(charge.getChargeId()));
        assertTrue(ex.getMessage().contains("Invalid cancellation method"));
    }

    @Test
    @DisplayName("Should list sent and received charges using pageable mapping")
    void listPaged_endpoints() {
        // Arrange
        UserModel current = user("33322211100");
        when(authentication.getCurrentUser()).thenReturn(current);
        
        Charge charge = new Charge(); 
        charge.setChargeId(UUID.randomUUID()); 
        charge.setAmount(BigDecimal.ONE);
        charge.setStatus(ChargeStatus.PENDING);
        charge.setOriginator(current);
        charge.setRecipient(user("99988877766"));
        
        Page<Charge> page = new PageImpl<>(List.of(charge));
        
        when(chargeRepository.findByOriginator(eq(current), any(Pageable.class))).thenReturn(page);
        when(chargeRepository.findByRecipient(eq(current), any(Pageable.class))).thenReturn(page);
        when(chargeRepository.findByRecipientAndStatus(eq(current), eq(ChargeStatus.PENDING), any(Pageable.class))).thenReturn(page);
        when(chargeRepository.findByOriginatorAndStatus(eq(current), eq(ChargeStatus.PENDING), any(Pageable.class))).thenReturn(page);

        // Act
        Page<ChargeResponse> sent = service.listSentCharges(0, 10, "desc");
        Page<ChargeResponse> received = service.listReceivedCharges(0, 10, "desc");
        Page<ChargeResponse> receivedPending = service.listReceivedChargesAndStatus(0, 10, "desc", "pending");
        Page<ChargeResponse> sentPending = service.listSentChargesAndStatus(0, 10, "asc", "pending");

        // Assert
        assertEquals(1, sent.getContent().size());
        assertEquals(1, received.getContent().size());
        assertEquals(1, receivedPending.getContent().size());
        assertEquals(1, sentPending.getContent().size());
        
        assertNotNull(sent.getContent().get(0).getChargeId());
        assertEquals(BigDecimal.ONE, sent.getContent().get(0).getAmount());
        assertEquals(ChargeStatus.PENDING, sent.getContent().get(0).getChargeStatus());
    }

    @Test
    @DisplayName("Should throw ObjectNotFoundException when charge not found for payment")
    void payCharge_notFound_throws() {
        // Arrange
        UserModel recipient = user("99988877766");
        when(authentication.getCurrentUser()).thenReturn(recipient);
        when(userService.findUserByCpf(recipient.getCpf())).thenReturn(recipient);
        when(chargeRepository.findByChargeIdAndRecipientId(any(UUID.class), eq(recipient.getUserId()))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ObjectNotFoundException.class, () -> service.paidWithBalance(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Should block paying non-pending charge")
    void payCharge_nonPending_throws() {
        // Arrange
        UserModel recipient = user("11100099988");
        UserModel originator = user("22211100099");
        when(authentication.getCurrentUser()).thenReturn(recipient);
        when(userService.findUserByCpf(recipient.getCpf())).thenReturn(recipient);

        Charge charge = pendingCharge(originator, recipient, new BigDecimal("12.00"));
        charge.setStatus(ChargeStatus.PAID);
        when(chargeRepository.findByChargeIdAndRecipientId(eq(charge.getChargeId()), eq(recipient.getUserId()))).thenReturn(Optional.of(charge));

        // Act & Assert
        assertThrows(ValidationException.class, () -> service.paidWithBalance(charge.getChargeId()));
    }

    @Test
    @DisplayName("Should cancel pending charge without refunding flows")
    void cancel_pendingCharge_onlyStatusUpdate() {
        // Arrange
        UserModel originator = user("12345098765");
        UserModel recipient = user("56789012345");
        when(authentication.getCurrentUser()).thenReturn(originator);
        when(userService.findUserByCpf(originator.getCpf())).thenReturn(originator);

        Charge charge = pendingCharge(originator, recipient, new BigDecimal("25.00"));
        when(chargeRepository.findByChargeIdAndOriginatortId(eq(charge.getChargeId()), eq(originator.getUserId())))
            .thenReturn(Optional.of(charge));

        // Act
        ChargeResponse resp = service.cancelBalanceCharge(charge.getChargeId());

        // Assert
        assertEquals(ChargeStatus.CANCELED, resp.getChargeStatus());
        verify(chargeRepository).save(any(Charge.class));
        verify(accountChargeService, never()).debitBalance(any(), any());
        verify(accountChargeService, never()).creditBalance(any(), any());
    }

    @Test
    @DisplayName("Should reject paying with card when charge is not pending")
    void paidWithCard_nonPending_throws() {
        UserModel recipient = user("11122233300");
        UserModel originator = user("00099988877");
        when(authentication.getCurrentUser()).thenReturn(recipient);
        when(userService.findUserByCpf(recipient.getCpf())).thenReturn(recipient);

        Charge charge = pendingCharge(originator, recipient, new BigDecimal("50.00"));
        charge.setStatus(ChargeStatus.CANCELED);
        when(chargeRepository.findByChargeIdAndRecipientId(eq(charge.getChargeId()), eq(recipient.getUserId())))
            .thenReturn(Optional.of(charge));

        assertThrows(ValidationException.class, () -> service.paidWithCard(charge.getChargeId(), new CardPaymentRequest()));
    }

    @Test
    @DisplayName("Should throw ObjectNotFoundException when originator cancels non-existent charge")
    void cancelCharge_notFound_throws() {
        UserModel originator = user("55544433322");
        when(authentication.getCurrentUser()).thenReturn(originator);
        when(userService.findUserByCpf(originator.getCpf())).thenReturn(originator);
        when(chargeRepository.findByChargeIdAndOriginatortId(any(UUID.class), eq(originator.getUserId())))
            .thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.cancelCardCharge(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Should list sent charges filtered by PAID and map correctly")
    void listSent_paidStatus_maps() {
        // Arrange
        UserModel current = user("12345678909");
        when(authentication.getCurrentUser()).thenReturn(current);
        
        Charge charge = new Charge();
        charge.setChargeId(UUID.randomUUID());
        charge.setAmount(new BigDecimal("9.99"));
        charge.setStatus(ChargeStatus.PAID);
        charge.setOriginator(current);
        charge.setRecipient(user("98765432100"));
        charge.setPaymentMethod(PaymentMethod.BALANCE);
        
        Page<Charge> page = new PageImpl<>(List.of(charge));
        
        when(chargeRepository.findByOriginatorAndStatus(eq(current), eq(ChargeStatus.PAID), any(Pageable.class))).thenReturn(page);

        Page<ChargeResponse> resp = service.listSentChargesAndStatus(0, 10, "asc", "PAID");
        
        // Assert
        assertEquals(1, resp.getContent().size());
        assertEquals(ChargeStatus.PAID, resp.getContent().get(0).getChargeStatus());
        assertEquals(new BigDecimal("9.99"), resp.getContent().get(0).getAmount());
    }

    @Test
    @DisplayName("Should set payment method BALANCE when paying with balance")
    void paidWithBalance_setsPaymentMethod() {
        UserModel recipient = user("90909090909");
        UserModel originator = user("80808080808");
        when(authentication.getCurrentUser()).thenReturn(recipient);
        when(userService.findUserByCpf(recipient.getCpf())).thenReturn(recipient);

        Charge charge = pendingCharge(originator, recipient, new BigDecimal("30.00"));
        when(chargeRepository.findByChargeIdAndRecipientId(eq(charge.getChargeId()), eq(recipient.getUserId())))
            .thenReturn(Optional.of(charge));

        Account originatorAccount = new Account(); originatorAccount.setAccountId(UUID.randomUUID());
        Account recipientAccount = new Account(); recipientAccount.setAccountId(UUID.randomUUID());
        when(accountChargeService.findAccountById(originator.getUserId())).thenReturn(originatorAccount);
        when(accountChargeService.findAccountById(recipient.getUserId())).thenReturn(recipientAccount);

        ChargeResponse resp = service.paidWithBalance(charge.getChargeId());
        assertEquals(ChargeStatus.PAID, resp.getChargeStatus());
    }

    @Test
    @DisplayName("Should throw when status string is invalid for list filters")
    void list_withInvalidStatus_throws() {
        // Arrange
        UserModel current = user("12121212121");
        when(authentication.getCurrentUser()).thenReturn(current);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.listReceivedChargesAndStatus(0, 5, "asc", "unknown"));
        assertThrows(IllegalArgumentException.class, () -> service.listSentChargesAndStatus(0, 5, "desc", "invalid"));
    }

    @Test
    @DisplayName("Should throw when canceling already canceled charge")
    void cancel_alreadyCanceled_throws() {
        // Arrange
        UserModel originator = user("33344455566");
        UserModel recipient = user("77788899900");
        when(authentication.getCurrentUser()).thenReturn(originator);
        when(userService.findUserByCpf(originator.getCpf())).thenReturn(originator);

        Charge charge = pendingCharge(originator, recipient, new java.math.BigDecimal("40.00"));
        charge.setStatus(ChargeStatus.CANCELED);
        when(chargeRepository.findByChargeIdAndOriginatortId(eq(charge.getChargeId()), eq(originator.getUserId())))
            .thenReturn(java.util.Optional.of(charge));

        // Act & Assert
        assertThrows(ValidationException.class, () -> service.cancelCardCharge(charge.getChargeId()));
        assertThrows(ValidationException.class, () -> service.cancelBalanceCharge(charge.getChargeId()));
    }

    @Test
    @DisplayName("Should refund balance-paid charge and register refund transaction")
    void cancelBalanceCharge_refundPaid() {
        // Arrange
        UserModel originator = user("14725836900");
        UserModel recipient = user("96385274100");
        when(authentication.getCurrentUser()).thenReturn(originator);
        when(userService.findUserByCpf(originator.getCpf())).thenReturn(originator);

        Charge charge = pendingCharge(originator, recipient, new BigDecimal("55.00"));
        charge.setStatus(ChargeStatus.PAID);
        charge.setPaymentMethod(PaymentMethod.BALANCE);
        when(chargeRepository.findByChargeIdAndOriginatortId(eq(charge.getChargeId()), eq(originator.getUserId())))
            .thenReturn(Optional.of(charge));

        Account originatorAccount = new Account(); originatorAccount.setAccountId(UUID.randomUUID());
        Account recipientAccount = new Account(); recipientAccount.setAccountId(UUID.randomUUID());
        when(accountChargeService.findAccountById(originator.getUserId())).thenReturn(originatorAccount);
        when(accountChargeService.findAccountById(recipient.getUserId())).thenReturn(recipientAccount);

        // Act
        ChargeResponse resp = service.cancelBalanceCharge(charge.getChargeId());

        // Assert
        assertEquals(ChargeStatus.CANCELED, resp.getChargeStatus());
        verify(accountChargeService).debitBalance(eq(originator.getUserId()), eq(new BigDecimal("55.00")));
        verify(accountChargeService).creditBalance(eq(recipient.getUserId()), eq(new BigDecimal("55.00")));
        verify(transactionService).registerBalanceRefund(eq(new BigDecimal("55.00")), eq(originatorAccount), eq(recipientAccount), eq(charge), eq(TransactionType.REFUND), eq(TransactionStatus.SUCCESS));
        verify(chargeRepository).save(any(Charge.class));
    }

    @Test
    @DisplayName("Should set paidAt timestamp when paying a charge")
    void pay_setsPaidAt() {
        // Arrange
        UserModel recipient = user("22233344466");
        UserModel originator = user("99900011122");
        when(authentication.getCurrentUser()).thenReturn(recipient);
        when(userService.findUserByCpf(recipient.getCpf())).thenReturn(recipient);

        Charge charge = pendingCharge(originator, recipient, new BigDecimal("10.50"));
        when(chargeRepository.findByChargeIdAndRecipientId(eq(charge.getChargeId()), eq(recipient.getUserId())))
            .thenReturn(Optional.of(charge));

        Account originatorAccount = new Account(); originatorAccount.setAccountId(UUID.randomUUID());
        Account recipientAccount = new Account(); recipientAccount.setAccountId(UUID.randomUUID());
        when(accountChargeService.findAccountById(originator.getUserId())).thenReturn(originatorAccount);
        when(accountChargeService.findAccountById(recipient.getUserId())).thenReturn(recipientAccount);

        // Act
        ChargeResponse resp = service.paidWithBalance(charge.getChargeId());

        // Assert
        assertEquals(ChargeStatus.PAID, resp.getChargeStatus());
        verify(chargeRepository).save(any(Charge.class));
    }

    @Test
    @DisplayName("Should set canceledAt timestamp when canceling a paid card charge")
    void cancel_setsCanceledAt() {
        // Arrange
        UserModel originator = user("55566677711");
        UserModel recipient = user("11122233344");
        when(authentication.getCurrentUser()).thenReturn(originator);
        when(userService.findUserByCpf(originator.getCpf())).thenReturn(originator);

        Charge charge = pendingCharge(originator, recipient, new BigDecimal("75.00"));
        charge.setStatus(ChargeStatus.PAID);
        charge.setPaymentMethod(PaymentMethod.CARD);
        when(chargeRepository.findByChargeIdAndOriginatortId(eq(charge.getChargeId()), eq(originator.getUserId())))
            .thenReturn(Optional.of(charge));

        // Act
        ChargeResponse resp = service.cancelCardCharge(charge.getChargeId());

        // Assert
        assertEquals(ChargeStatus.CANCELED, resp.getChargeStatus());
        verify(chargeRepository).save(any(Charge.class));
    }
}