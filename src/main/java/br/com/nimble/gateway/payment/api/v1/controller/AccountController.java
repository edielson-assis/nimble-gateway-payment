package br.com.nimble.gateway.payment.api.v1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.nimble.gateway.payment.api.v1.doc.AccountControllerDocs;
import br.com.nimble.gateway.payment.api.v1.dto.request.AccountRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.AccountResponse;
import br.com.nimble.gateway.payment.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController implements AccountControllerDocs {
    
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> deposit(
            @RequestBody @Valid AccountRequest accountRequest) {
        var response = accountService.deposit(accountRequest.getAmount());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}