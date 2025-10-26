package br.com.nimble.gateway.payment.api.v1.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.nimble.gateway.payment.api.v1.doc.TransactionControllerDocs;
import br.com.nimble.gateway.payment.api.v1.dto.response.TransactionResponse;
import br.com.nimble.gateway.payment.service.TransactionProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController implements TransactionControllerDocs {

    private final TransactionProvider transactionProvider;
    
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> listAllTransactionByUser(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "asc") String direction) {
        var charges = transactionProvider.listAllTransactionByUser(page, size, direction);
        return new ResponseEntity<>(charges, HttpStatus.OK);
    }
}