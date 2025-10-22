package br.com.nimble.gateway.payment.api.v1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.nimble.gateway.payment.api.v1.doc.AuthenticationControllerDocs;
import br.com.nimble.gateway.payment.api.v1.dto.request.UserSigninRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.UserSignupRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.TokenAndRefreshTokenResponse;
import br.com.nimble.gateway.payment.api.v1.dto.response.TokenResponse;
import br.com.nimble.gateway.payment.api.v1.dto.response.UserResponse;
import br.com.nimble.gateway.payment.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController implements AuthenticationControllerDocs {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(
            @RequestBody @Valid UserSignupRequest userRequest) {
        var user = userService.saveCommonUser(userRequest);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

	@PostMapping(path = "/signin")	
    public ResponseEntity<TokenAndRefreshTokenResponse> signin(
            @RequestBody @Valid UserSigninRequest userRequest) {
		var token = userService.signin(userRequest);
		return new ResponseEntity<>(token, HttpStatus.OK);
	}
	
	@GetMapping(path = "/refresh/{email}")
	public ResponseEntity<TokenResponse> refreshToken(
            @PathVariable("email") String email, 
            @RequestHeader("Authorization") String refreshToken) {
        var token = userService.refreshToken(email, refreshToken);
		return new ResponseEntity<>(token, HttpStatus.OK);
	}
}