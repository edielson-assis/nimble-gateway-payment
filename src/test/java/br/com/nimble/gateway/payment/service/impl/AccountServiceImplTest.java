package br.com.nimble.gateway.payment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.nimble.gateway.payment.api.v1.dto.response.AccountResponse;
import br.com.nimble.gateway.payment.config.security.context.AuthenticatedUserProvider;
import br.com.nimble.gateway.payment.domain.exception.ObjectNotFoundException;
import br.com.nimble.gateway.payment.domain.model.Account;
import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionStatus;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionType;
import br.com.nimble.gateway.payment.domain.repository.AccountRepository;
import br.com.nimble.gateway.payment.integration.AuthorizerAdapter;
import br.com.nimble.gateway.payment.service.TransactionService;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AuthorizerAdapter authorizerAdapter;
    @Mock
    private AuthenticatedUserProvider authentication;
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private static class AuthUser extends UserModel {
        private final UUID id;
        AuthUser(UUID id) { this.id = id; }
        @Override public UUID getUserId() { return id; }
    }

    private Account mockAccount(UUID userId, BigDecimal balance) {
        UserModel user = new UserModel();
        user.setUserId(userId);
        Account account = new Account();
        account.setAccountId(UUID.randomUUID());
        account.setUser(user);
        account.deposit(balance);
        return account;
    }

    @Test
    @DisplayName("Should create account for a given user and persist it")
    void shouldCreateAccount() {
        // Arrange
        UserModel user = new UserModel();
        UUID userId = UUID.randomUUID();
        user.setUserId(userId);
        Account toSave = new Account();
        when(accountRepository.save(any(Account.class))).thenReturn(toSave);

        // Act
        Account saved = accountService.createAccount(user);

        // Assert
        assertNotNull(saved);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Should deposit when authorized, update balance and register transaction")
    void shouldDepositWhenAuthorized() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(authentication.getCurrentUser()).thenReturn(new AuthUser(userId));
        Account existing = mockAccount(userId, new BigDecimal("100.00"));
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        BigDecimal amount = new BigDecimal("50.00");

        // Act
        AccountResponse response = accountService.deposit(amount);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("150.00"), response.getBalance());
        verify(authorizerAdapter).isAuthorizedTransaction(any(AccountResponse.class), eq(amount), eq(TransactionType.DEPOSIT));
        verify(transactionService).registerDeposit(eq(amount), eq(existing), eq(TransactionType.DEPOSIT), eq(TransactionStatus.SUCCESS));
        verify(accountRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw when paying with balance and funds are insufficient")
    void shouldThrowWhenPayWithInsufficientFunds() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(authentication.getCurrentUser()).thenReturn(new AuthUser(userId));
        Account existing = mockAccount(userId, new BigDecimal("10.00"));
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> accountService.payWithBalance(new BigDecimal("20.00")));
        verify(authorizerAdapter, never()).isAuthorizedTransaction(any(AccountResponse.class), any(BigDecimal.class), any(TransactionType.class));
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should pay with balance when authorized and update repository")
    void shouldPayWithBalanceWhenAuthorized() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(authentication.getCurrentUser()).thenReturn(new AuthUser(userId));
        Account existing = mockAccount(userId, new BigDecimal("100.00"));
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        BigDecimal amount = new BigDecimal("30.00");

        // Act
        accountService.payWithBalance(amount);

        // Assert
        assertEquals(new BigDecimal("70.00"), existing.getBalance());
        verify(authorizerAdapter).isAuthorizedTransaction(any(AccountResponse.class), eq(amount), eq(TransactionType.PAYMENT));
        verify(accountRepository).save(existing);
    }

    @Test
    @DisplayName("Should credit balance for given user and persist")
    void shouldCreditBalance() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Account existing = mockAccount(userId, new BigDecimal("40.00"));
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        accountService.creditBalance(userId, new BigDecimal("10.00"));

        // Assert
        assertEquals(new BigDecimal("50.00"), existing.getBalance());
        verify(accountRepository).save(existing);
    }

    @Test
    @DisplayName("Should check balance of current user and return DTO")
    void shouldCheckBalance() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(authentication.getCurrentUser()).thenReturn(new AuthUser(userId));
        Account existing = mockAccount(userId, new BigDecimal("77.00"));
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        // Act
        AccountResponse response = accountService.checkBalance();

        // Assert
        assertEquals(new BigDecimal("77.00"), response.getBalance());
    }

    @Test
    @DisplayName("Should debit balance for a user when sufficient funds")
    void shouldDebitBalance() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Account existing = mockAccount(userId, new BigDecimal("90.00"));
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        // Act
        accountService.debitBalance(userId, new BigDecimal("20.00"));

        // Assert
        assertEquals(new BigDecimal("70.00"), existing.getBalance());
        verify(accountRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw when debiting and there is no balance")
    void shouldThrowWhenDebitWithoutBalance() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Account existing = mockAccount(userId, new BigDecimal("5.00"));
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> accountService.debitBalance(userId, new BigDecimal("10.00")));
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find account by id when present, else throw ObjectNotFoundException")
    void shouldFindAccountOrThrow() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Account acc = mockAccount(userId, BigDecimal.ZERO);
        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(acc));

        // Act
        Account found = accountService.findAccountById(userId);
        assertNotNull(found);

        // Arrange not found
        UUID missing = UUID.randomUUID();
        when(accountRepository.findByUserId(missing)).thenReturn(Optional.empty());

        // Assert not found
        assertThrows(ObjectNotFoundException.class, () -> accountService.findAccountById(missing));
    }
}