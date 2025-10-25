package br.com.nimble.gateway.payment.api.v1.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.nimble.gateway.payment.api.v1.doc.ChargeControllerDocs;
import br.com.nimble.gateway.payment.api.v1.dto.request.CardPaymentRequest;
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

    @PutMapping("/{chargeId}/balance")
    public ResponseEntity<ChargeResponse> paidWithBalance(@PathVariable UUID chargeId) {
        var charge = chargeService.paidWithBalance(chargeId);
        return new ResponseEntity<>(charge, HttpStatus.OK);
    }

    @PutMapping("/{chargeId}/card")
    public ResponseEntity<ChargeResponse> paidWithCard(
            @PathVariable UUID chargeId,
            @RequestBody CardPaymentRequest card) {
        var charge = chargeService.paidWithCard(chargeId, card);
        return new ResponseEntity<>(charge, HttpStatus.OK);
    }

    @PutMapping("/{chargeId}/balance/cancel")
    public ResponseEntity<ChargeResponse> cancelBalanceCharge(@PathVariable UUID chargeId) {
        var charge = chargeService.cancelBalanceCharge(chargeId);
        return new ResponseEntity<>(charge, HttpStatus.OK);
    }

    @PutMapping("/{chargeId}/card/cancel")
    public ResponseEntity<ChargeResponse> cancelCardCharge(@PathVariable UUID chargeId) {
        var charge = chargeService.cancelCardCharge(chargeId);
        return new ResponseEntity<>(charge, HttpStatus.OK);
    }

    @GetMapping("/sent")
    public ResponseEntity<Page<ChargeResponse>> listSentCharges(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "asc") String direction) {
        var charges = chargeService.listSentCharges(page, size, direction);
        return new ResponseEntity<>(charges, HttpStatus.OK);
    }

    @GetMapping("/received")
    public ResponseEntity<Page<ChargeResponse>> listReceivedCharges(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "asc") String direction) {
        var charges = chargeService.listReceivedCharges(page, size, direction);
        return new ResponseEntity<>(charges, HttpStatus.OK);
    }

    @GetMapping("/sent/{status}")
    public ResponseEntity<Page<ChargeResponse>> listSentChargesAndStatus(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "asc") String direction,
            @PathVariable String status) {
        var charges = chargeService.listSentChargesAndStatus(page, size, direction, status);
        return new ResponseEntity<>(charges, HttpStatus.OK);
    }

    @GetMapping("/received/{status}")
    public ResponseEntity<Page<ChargeResponse>> listReceivedChargesAndStatus(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "asc") String direction,
            @PathVariable String status) {
        var charges = chargeService.listReceivedChargesAndStatus(page, size, direction, status);
        return new ResponseEntity<>(charges, HttpStatus.OK);
    }
}