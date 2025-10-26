package br.com.nimble.gateway.payment.service;

import java.math.BigDecimal;

import br.com.nimble.gateway.payment.domain.model.Account;
import br.com.nimble.gateway.payment.domain.model.CardPayment;
import br.com.nimble.gateway.payment.domain.model.Charge;
import br.com.nimble.gateway.payment.domain.model.Transaction;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionStatus;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionType;

public interface TransactionService {
    
    Transaction registerBalancePaid(BigDecimal amount, Account originator, Account recipient, Charge charge, TransactionType type, TransactionStatus status);

    Transaction registerCardPaid(BigDecimal amount, Charge charge, CardPayment card, TransactionType type, TransactionStatus status);
    
    Transaction registerBalanceRefund(BigDecimal amount, Account recipient, Account originator, Charge charge, TransactionType type, TransactionStatus status);

    Transaction registerCardRefund(BigDecimal amount, Charge charge, TransactionType type, TransactionStatus status);

    Transaction registerDeposit(BigDecimal amount, Account account, TransactionType type, TransactionStatus status);
}