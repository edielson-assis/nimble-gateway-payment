package br.com.nimble.gateway.payment.integration;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.nimble.gateway.payment.api.v1.dto.response.AuthorizerResponse;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionType;

@FeignClient(name = "authorizer", url = "${authorizer.url}")
public interface AuthorizerClient {

    @GetMapping
    AuthorizerResponse authorizeTransaction(
        @RequestParam Object request,
        @RequestParam BigDecimal amount,
        @RequestParam TransactionType type
    );
}