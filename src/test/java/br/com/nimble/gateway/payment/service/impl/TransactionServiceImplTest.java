package br.com.nimble.gateway.payment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
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

import br.com.nimble.gateway.payment.api.v1.dto.response.TransactionResponse;
import br.com.nimble.gateway.payment.config.security.context.AuthenticatedUserProvider;
import br.com.nimble.gateway.payment.domain.model.Account;
import br.com.nimble.gateway.payment.domain.model.CardPayment;
import br.com.nimble.gateway.payment.domain.model.Charge;
import br.com.nimble.gateway.payment.domain.model.Transaction;
import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.model.enums.ChargeStatus;
import br.com.nimble.gateway.payment.domain.model.enums.PaymentMethod;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionStatus;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionType;
import br.com.nimble.gateway.payment.domain.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuthenticatedUserProvider authentication;

    @InjectMocks
    private TransactionServiceImpl service;

    private UserModel user;
    private Account account;
    private Account recipientAccount;
    private Charge charge;
    private CardPayment cardPayment;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        // Configurar usuário
        user = new UserModel();
        user.setUserId(UUID.randomUUID());
        user.setCpf("12345678900");
        
        // Configurar conta
        account = new Account();
        account.setAccountId(UUID.randomUUID());
        account.setUser(user);
        account.deposit(new BigDecimal("1000.00"));
        
        // Configurar conta do destinatário
        recipientAccount = new Account();
        recipientAccount.setAccountId(UUID.randomUUID());
        recipientAccount.setUser(createUser("98765432100"));
        recipientAccount.deposit(new BigDecimal("500.00"));
        
        // Configurar cobrança
        charge = new Charge();
        charge.setChargeId(UUID.randomUUID());
        charge.setAmount(new BigDecimal("100.00"));
        charge.setStatus(ChargeStatus.PAID);
        charge.setOriginator(user);
        charge.setRecipient(recipientAccount.getUser());
        charge.setPaymentMethod(PaymentMethod.BALANCE);
        
        // Configurar pagamento com cartão
        cardPayment = new CardPayment();
        cardPayment.setCardPaymentId(UUID.randomUUID());
        cardPayment.setCharge(charge);
        cardPayment.setCardHolderName("John Doe");
        cardPayment.setCardLast4("1234");
        
        // Configurar transação
        transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.PAYMENT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCharge(charge);
    }

    @Test
    @DisplayName("Should register balance payment transaction successfully")
    void registerBalancePaid_success() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction result = service.registerBalancePaid(
            amount, 
            account, 
            recipientAccount, 
            charge, 
            TransactionType.PAYMENT, 
            TransactionStatus.SUCCESS
        );

        // Assert
        assertNotNull(result);
        assertEquals(transaction.getTransactionId(), result.getTransactionId());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should register card payment transaction successfully")
    void registerCardPaid_success() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction result = service.registerCardPaid(
            amount,
            charge,
            cardPayment,
            TransactionType.PAYMENT,
            TransactionStatus.SUCCESS
        );

        // Assert
        assertNotNull(result);
        assertEquals(transaction.getTransactionId(), result.getTransactionId());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should register balance refund transaction successfully")
    void registerBalanceRefund_success() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction result = service.registerBalanceRefund(
            amount,
            account,
            recipientAccount,
            charge,
            TransactionType.REFUND,
            TransactionStatus.SUCCESS
        );

        // Assert
        assertNotNull(result);
        assertEquals(transaction.getTransactionId(), result.getTransactionId());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should register card refund transaction successfully")
    void registerCardRefund_success() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction result = service.registerCardRefund(
            amount,
            charge,
            TransactionType.REFUND,
            TransactionStatus.SUCCESS
        );

        // Assert
        assertNotNull(result);
        assertEquals(transaction.getTransactionId(), result.getTransactionId());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should register deposit transaction successfully")
    void registerDeposit_success() {
        // Arrange
        BigDecimal amount = new BigDecimal("200.00");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction result = service.registerDeposit(
            amount,
            account,
            TransactionType.DEPOSIT,
            TransactionStatus.SUCCESS
        );

        // Assert
        assertNotNull(result);
        assertEquals(transaction.getTransactionId(), result.getTransactionId());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should list all transactions by user with DESC ordering")
    void listAllTransactionByUser_descOrder() {
        // Arrange
        int page = 0;
        int size = 10;
        String direction = "desc";

        when(authentication.getCurrentUser()).thenReturn(user);

        Transaction transaction1 = createTransaction(new BigDecimal("100.00"), TransactionType.PAYMENT);
        Transaction transaction2 = createTransaction(new BigDecimal("50.00"), TransactionType.REFUND);
        transaction1.setFromAccount(account);
        transaction1.setToAccount(recipientAccount);
        transaction2.setFromAccount(account);
        transaction2.setToAccount(recipientAccount);

        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction1, transaction2));

        when(transactionRepository.findByUserTransaction(
            eq(user.getUserId()),
            any(Pageable.class)
        )).thenReturn(transactionPage);

        // Act
        Page<TransactionResponse> result = service.listAllTransactionByUser(page, size, direction);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(transactionRepository).findByUserTransaction(
            eq(user.getUserId()),
            any(Pageable.class)
        );
    }

    @Test
    @DisplayName("Should list all transactions by user with ASC ordering")
    void listAllTransactionByUser_ascOrder() {
        // Arrange
        int page = 0;
        int size = 10;
        String direction = "asc";

        when(authentication.getCurrentUser()).thenReturn(user);

        Transaction transaction1 = createTransaction(new BigDecimal("100.00"), TransactionType.PAYMENT);
        Transaction transaction2 = createTransaction(new BigDecimal("50.00"), TransactionType.REFUND);
        transaction1.setFromAccount(account);
        transaction1.setToAccount(recipientAccount);
        transaction2.setFromAccount(account);
        transaction2.setToAccount(recipientAccount);

        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction1, transaction2));

        when(transactionRepository.findByUserTransaction(
            eq(user.getUserId()),
            any(Pageable.class)
        )).thenReturn(transactionPage);

        // Act
        Page<TransactionResponse> result = service.listAllTransactionByUser(page, size, direction);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(transactionRepository).findByUserTransaction(
            eq(user.getUserId()),
            any(Pageable.class)
        );
    }
    
    @Test
    @DisplayName("Should handle empty transaction list")
    void listAllTransactionByUser_emptyList() {
        // Arrange
        int page = 0;
        int size = 10;
        String direction = "desc";
        
        when(authentication.getCurrentUser()).thenReturn(user);
        
        Page<Transaction> emptyPage = new PageImpl<>(List.of());
        
        when(transactionRepository.findByUserTransaction(
            eq(user.getUserId()),
            any(Pageable.class)
        )).thenReturn(emptyPage);

        // Act
        Page<TransactionResponse> result = service.listAllTransactionByUser(page, size, direction);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
    }

    private UserModel createUser(String cpf) {
        UserModel user = new UserModel();
        user.setUserId(UUID.randomUUID());
        user.setCpf(cpf);
        return user;
    }
    
    private Transaction createTransaction(BigDecimal amount, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCharge(charge);
        return transaction;
    }
}