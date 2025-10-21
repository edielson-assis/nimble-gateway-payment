package br.com.nimble.gateway.payment.config.security;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.com.nimble.gateway.payment.api.v1.dto.response.TokenAndRefreshTokenResponse;
import br.com.nimble.gateway.payment.api.v1.dto.response.TokenResponse;
import br.com.nimble.gateway.payment.service.AuthService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class JwtTokenProvider {

    private static final String ROLES = "roles";
    private static final String BEARER = "Bearer ";
    private static final String ZONE_ID = "America/Sao_Paulo";

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expiration-length}")
    private int expirationToken;

    @Value("${security.jwt.token.refresh-expiration-length}")
    private int refreshExpirationToken;

    private final AuthService authService;
    private Algorithm algorithm;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        algorithm = Algorithm.HMAC256(secretKey.getBytes());
    }

    public TokenAndRefreshTokenResponse createAccessTokenRefreshToken(String username, List<String> roles) {
		log.info("Generating access token for user: {}", username);
        Instant now = Instant.now();
        Instant expiration = calculateExpirationToken();

        var accessToken = createToken(username, roles, now, expiration);
        var refreshToken = createRefreshToken(username, roles, now);
		log.debug("Access token and refresh token generated for user: {}", username);
        return new TokenAndRefreshTokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refreshToken(String refreshToken, String username) {
        refreshToken = stripBearerPrefix(refreshToken);
        DecodedJWT decodedJWT = verifyToken(refreshToken);
        String user = decodedJWT.getSubject();
		verifyUsername(username, decodedJWT);
        List<String> roles = decodedJWT.getClaim(ROLES).asList(String.class);
		log.debug("Token refreshed for user: {}", user);
        return createAccessToken(user, roles);
    }

    public Authentication getAuthentication(String token) {
		log.info("Getting authentication from token");
        DecodedJWT decodedJWT = verifyToken(token);
        UserDetails userDetails = authService.loadUserByUsername(decodedJWT.getSubject());
        log.debug("User authenticated: {}", decodedJWT.getSubject());
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
		log.info("Resolving token from request header");
        if (bearerToken != null && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEARER.length());
        }
        return null;
    }

    public boolean validateToken(String token) {
		log.info("Validating token");
        DecodedJWT decodedJWT = verifyToken(token);
        return !decodedJWT.getExpiresAt().before(new Date());
    }

	private TokenResponse createAccessToken(String username, List<String> roles) {
		log.info("Generating access token for user: {}", username);
        Instant now = Instant.now();
        Instant expiration = calculateExpirationToken();

        var accessToken = createToken(username, roles, now, expiration);
		log.debug("Access token and refresh token generated for user: {}", username);
        return new TokenResponse(accessToken);
    }

    private String createToken(String username, List<String> roles, Instant now, Instant expiration) {
        log.info("Creating access token for user: {}", username);
		String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return JWT.create()
                .withClaim(ROLES, roles)
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .withSubject(username)
                .withIssuer(issuerUrl)
                .sign(algorithm);
    }

    private String createRefreshToken(String username, List<String> roles, Instant now) {
        log.info("Creating refresh token for user: {}", username);
		Instant expiration = calculateExpirationRefreshToken();
        return JWT.create()
                .withClaim(ROLES, roles)
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .withSubject(username)
                .sign(algorithm);
    }

    private Instant calculateExpirationToken() {
		log.info("Calculating expiration time for access token");
        return ZonedDateTime.now(ZoneId.of(ZONE_ID)).plusHours(expirationToken).toInstant();
    }

    private Instant calculateExpirationRefreshToken() {
		log.info("Calculating expiration time for refresh token");
        return ZonedDateTime.now(ZoneId.of(ZONE_ID)).plusDays(refreshExpirationToken).toInstant();
    }

    private DecodedJWT verifyToken(String token) {
		log.info("Decoding token");
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    private String stripBearerPrefix(String token) {
        if (token.startsWith(BEARER)) {
            return token.substring(BEARER.length());
        }
        return token;
    }

	private void verifyUsername(String username, DecodedJWT decodedJWT) {
		String tokenUsername = decodedJWT.getSubject();
		if (!username.equals(tokenUsername)) {
			log.error("Error during refresh token generation for user: " + username);
			throw new BadCredentialsException("Failed to refresh the token for user: " + username);
		}
	}
}