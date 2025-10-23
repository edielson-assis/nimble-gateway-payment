package br.com.nimble.gateway.payment.service;

import br.com.nimble.gateway.payment.api.v1.dto.request.ChargeRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.ChargeResponse;

public interface ChargeService {

    ChargeResponse createCharge(ChargeRequest chargeRequest);
}