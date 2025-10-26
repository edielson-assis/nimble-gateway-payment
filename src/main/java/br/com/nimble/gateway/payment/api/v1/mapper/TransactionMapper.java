package br.com.nimble.gateway.payment.api.v1.mapper;

import java.math.BigDecimal;

import br.com.nimble.gateway.payment.domain.model.Account;
import br.com.nimble.gateway.payment.domain.model.Charge;
import br.com.nimble.gateway.payment.domain.model.Transaction;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionStatus;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionType;

public class TransactionMapper {
    
    private TransactionMapper() {}

    public static Transaction toEntity(BigDecimal amount, Account originator, Account recipient, Charge charge, TransactionType type, TransactionStatus status) {
        var transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setFromAccount(originator);
        transaction.setToAccount(recipient);
        transaction.setCharge(charge);
        transaction.setType(type);
        transaction.setStatus(status);
        return transaction;
    }

    public static Transaction toEntity(BigDecimal amount, Charge charge, TransactionType type, TransactionStatus status) {
        var transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setCharge(charge);
        transaction.setType(type);
        transaction.setStatus(status);
        return transaction;
    }

    public static Transaction toEntity(BigDecimal amount, Account recipient, Account originator, Charge charge, TransactionType type) {
        var transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setFromAccount(originator);
        transaction.setToAccount(recipient);
        transaction.setCharge(charge);
        transaction.setType(type);
        return transaction;
    }

    public static Transaction toEntity(BigDecimal amount, Account account, TransactionType type, TransactionStatus status) {
        var transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setStatus(status);
        transaction.setToAccount(account);
        return transaction;
    }
}