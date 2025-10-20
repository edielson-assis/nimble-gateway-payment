package br.com.nimble.gateway.payment.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    
    UserDetails loadUserByUsername(String email);
}