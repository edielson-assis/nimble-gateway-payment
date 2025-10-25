package br.com.nimble.gateway.payment.integration;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.nimble.gateway.payment.api.v1.dto.response.AuthorizerResponse;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionType;

@FeignClient(name = "authorizer", url = "https://zsy6tx7aql.execute-api.sa-east-1.amazonaws.com")
public interface AuthorizerClient {
    
    @GetMapping("/authorizer")
    AuthorizerResponse authorizeTransaction(Object request, BigDecimal amount, TransactionType type);
}