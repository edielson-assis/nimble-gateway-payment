package br.com.nimble.gateway.payment.integration;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import br.com.nimble.gateway.payment.api.v1.dto.response.AuthorizerResponse;
import br.com.nimble.gateway.payment.domain.model.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Component
public final class AuthorizerAdapter {

    private final AuthorizerClient authorizerClient;

    public final AuthorizerResponse isAuthorizedTransaction(Object request, BigDecimal amount, TransactionType type) {
        var response = authorizerClient.authorizeTransaction(request, amount, type);
        if (response == null || response.data() == null || !Boolean.TRUE.equals(response.data().authorized())) {
            log.info("Transaction not authorized by external service.");
            throw new IllegalArgumentException("Transaction not authorized");
        }
        return response;
    }
}