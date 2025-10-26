package br.com.nimble.gateway.payment.service;

import org.springframework.data.domain.Page;

import br.com.nimble.gateway.payment.api.v1.dto.response.TransactionResponse;

public interface TransactionProvider {
    
    Page<TransactionResponse> listAllTransactionByUser(Integer page, Integer size, String direction);
}