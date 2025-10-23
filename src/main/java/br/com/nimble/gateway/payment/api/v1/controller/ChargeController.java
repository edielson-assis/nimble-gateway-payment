package br.com.nimble.gateway.payment.api.v1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.nimble.gateway.payment.api.v1.doc.ChargeControllerDocs;
import br.com.nimble.gateway.payment.api.v1.dto.request.ChargeRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.ChargeResponse;
import br.com.nimble.gateway.payment.service.ChargeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/charges")
public class ChargeController implements ChargeControllerDocs {
    
    private final ChargeService chargeService;

    @PostMapping
    public ResponseEntity<ChargeResponse> createCharge(
            @RequestBody @Valid ChargeRequest chargeRequest) {
        var charge = chargeService.createCharge(chargeRequest);
        return new ResponseEntity<>(charge, HttpStatus.CREATED);
    }
}