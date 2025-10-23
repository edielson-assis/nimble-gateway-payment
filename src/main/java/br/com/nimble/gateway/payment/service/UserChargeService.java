package br.com.nimble.gateway.payment.service;

import br.com.nimble.gateway.payment.domain.model.UserModel;

public interface UserChargeService {
    
    UserModel findUserByCpf(String cpf);
}