package br.com.nimble.gateway.payment.api.v1.mapper;

import br.com.nimble.gateway.payment.api.v1.dto.request.ChargeRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.ChargeResponse;
import br.com.nimble.gateway.payment.domain.model.Charge;

public class ChargeMapper {
    
    private ChargeMapper() {}

    public static Charge toEntity(ChargeRequest chargeRequest) {
        var charge = new Charge();
        charge.setAmount(chargeRequest.getAmount());
        charge.setDescription(chargeRequest.getDescription());
        return charge;
    }

    public static ChargeResponse toDto(Charge charge) {
        var ChargeResponse = new ChargeResponse();
        ChargeResponse.setChargeId(charge.getChargeId());
        ChargeResponse.setAmount(charge.getAmount());
        ChargeResponse.setDescription(charge.getDescription());
        ChargeResponse.setChargeStatus(charge.getStatus());
        ChargeResponse.setCreatedAt(charge.getCreatedAt());
        ChargeResponse.setOriginator(UserMapper.toDto(charge.getOriginator()));
        ChargeResponse.setRecipient(UserMapper.toDto(charge.getRecipient()));
        return ChargeResponse;
    }
}