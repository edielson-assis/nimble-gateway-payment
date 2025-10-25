package br.com.nimble.gateway.payment.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import br.com.nimble.gateway.payment.api.v1.dto.request.CardPaymentRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.ChargeRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.ChargeResponse;

public interface ChargeService {

    ChargeResponse createCharge(ChargeRequest chargeRequest);

    ChargeResponse paidWithBalance(UUID chargeId);

    ChargeResponse paidWithCard(UUID chargeId, CardPaymentRequest card);

    ChargeResponse cancelCardCharge(UUID chargeId);

    ChargeResponse cancelBalanceCharge(UUID chargeId);

    Page<ChargeResponse> listSentCharges(Integer page, Integer size, String direction);

    Page<ChargeResponse> listReceivedCharges(Integer page, Integer size, String direction);

    Page<ChargeResponse> listSentChargesAndStatus(Integer page, Integer size, String direction, String status);

    Page<ChargeResponse> listReceivedChargesAndStatus(Integer page, Integer size, String direction, String status);
}