package br.com.nimble.gateway.payment.config.security.context;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import br.com.nimble.gateway.payment.api.v1.dto.request.UserSigninRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.TokenAndRefreshTokenResponse;
import br.com.nimble.gateway.payment.config.security.JwtTokenProvider;
import br.com.nimble.gateway.payment.domain.model.UserModel;
import br.com.nimble.gateway.payment.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Component
public class AuthenticatedUserProviderImpl implements AuthenticatedUserProvider {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenAndRefreshTokenResponse authenticateUser(UserSigninRequest userDto) {
        var email = userDto.getEmail();
        var cpf = userDto.getCpf();
        var username = email != null ? email : cpf;
		try {
			log.debug("Authenticating user with username: {}", username);
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, userDto.getPassword()));
			log.debug("Authentication successful for user: {}", username);
			var user = findByEmailOrCpf(email, cpf);
			log.info("Generating access and refresh token for user: {}", username);
			return tokenProvider.createAccessTokenRefreshToken(user.getUsername(), user.getRoles());
		} catch (Exception e) {
			log.error("Invalid username or password for user: {}", username);
			throw new BadCredentialsException("Invalid username or password");
		}
    }

    @Override
    public UserModel getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserModel) authentication.getPrincipal();
    }   

    private UserModel findByEmailOrCpf(String email, String cpf) {
        log.info("Verifying the user's email or CPF");
        return userRepository.findByEmailOrCpf(email, cpf).orElse(null);
    }
}