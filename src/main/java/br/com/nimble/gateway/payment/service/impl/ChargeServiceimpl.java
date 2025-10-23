package br.com.nimble.gateway.payment.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.nimble.gateway.payment.api.v1.dto.request.ChargeRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.ChargeResponse;
import br.com.nimble.gateway.payment.api.v1.mapper.ChargeMapper;
import br.com.nimble.gateway.payment.config.security.context.AuthenticatedUserProvider;
import br.com.nimble.gateway.payment.domain.exception.ValidationException;
import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.model.enums.ChargeStatus;
import br.com.nimble.gateway.payment.domain.repository.ChargeRepository;
import br.com.nimble.gateway.payment.service.ChargeService;
import br.com.nimble.gateway.payment.service.UserChargeService;
import br.com.nimble.gateway.payment.util.LoginUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class ChargeServiceimpl implements ChargeService {

    private final ChargeRepository chargeRepository;
    private final AuthenticatedUserProvider authentication;
    private final UserChargeService userService;

    @Transactional
    @Override
    public ChargeResponse createCharge(ChargeRequest chargeRequest) {
        var charge = ChargeMapper.toEntity(chargeRequest);
        charge.setOriginator(authentication.getCurrentUser());
        var cpf = LoginUtils.normalizeCpf(chargeRequest.getRecipientCpf());
        charge.setRecipient(findRecipientByCpf(cpf));
        charge.setStatus(ChargeStatus.PENDING);
        validateifOriginatorIsNotRecipient(charge.getOriginator(), charge.getRecipient());
        log.info("Creating charge to recipient's CPF: {}", charge.getRecipient().getCpf());
        chargeRepository.save(charge);
        return ChargeMapper.toDto(charge);
    }

    private UserModel findRecipientByCpf(String cpf) {
        return userService.findUserByCpf(cpf);
    } 
    
    private void validateifOriginatorIsNotRecipient(UserModel originator, UserModel recipient) {
        if (originator.getCpf().equals(recipient.getCpf())) {
            log.error("The originator cannot be the recipient of the charge");
            throw new ValidationException("The originator cannot be the recipient of the charge");
        }
    }
}