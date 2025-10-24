package br.com.nimble.gateway.payment.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.nimble.gateway.payment.api.v1.dto.response.AuthorizerResponse;

@FeignClient(name = "authorizer", url = "https://zsy6tx7aql.execute-api.sa-east-1.amazonaws.com")
public interface AuthorizerService {
    
    @GetMapping("/authorizer")
    AuthorizerResponse authorizeTransaction();
}