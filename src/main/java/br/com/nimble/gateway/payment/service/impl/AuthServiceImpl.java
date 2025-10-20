package br.com.nimble.gateway.payment.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.nimble.gateway.payment.domain.repository.UserRepository;
import br.com.nimble.gateway.payment.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String emailOrCpf) {
        log.info("Verifying the user's email or CPF: {}", emailOrCpf);
        return repository.findByEmailOrCpf(emailOrCpf).orElseThrow(() -> {
            log.error("Email or CPF not found: {}", emailOrCpf);
            return new UsernameNotFoundException("Email or CPF not found");
        });    
    }
}