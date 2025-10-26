package br.com.nimble.gateway.payment.domain.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.nimble.gateway.payment.domain.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("""
        SELECT t
        FROM Transaction t
        WHERE t.fromAccount.user.userId = :userId
            OR t.toAccount.user.userId = :userId
        ORDER BY t.createdAt DESC
    """)
    Page<Transaction> findByUserTransaction(@Param("userId") UUID userId, Pageable pageable);
}