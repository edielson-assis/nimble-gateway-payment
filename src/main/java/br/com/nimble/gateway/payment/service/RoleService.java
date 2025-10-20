package br.com.nimble.gateway.payment.service;

import br.com.nimble.gateway.payment.domain.model.RoleModel;

public interface RoleService {

    RoleModel findByRole(String name);
}