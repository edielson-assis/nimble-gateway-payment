package br.com.nimble.gateway.payment.service.impl;

import org.springframework.stereotype.Service;

import br.com.nimble.gateway.payment.domain.exception.ObjectNotFoundException;
import br.com.nimble.gateway.payment.domain.model.RoleModel;
import br.com.nimble.gateway.payment.domain.repository.RoleRepository;
import br.com.nimble.gateway.payment.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    
    private final RoleRepository repository;

    @Override
    public RoleModel findByRole(String name) {
        log.info("Verifying for role: {}", name);
        return repository.findByRoleName(name).orElseThrow(() -> {
            log.error("Role not found: {}", name);
            return new ObjectNotFoundException("Role not found");
        });
    }
}