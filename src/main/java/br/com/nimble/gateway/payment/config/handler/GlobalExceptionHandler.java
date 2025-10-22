package br.com.nimble.gateway.payment.config.handler;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;

import br.com.nimble.gateway.payment.domain.exception.ObjectNotFoundException;
import br.com.nimble.gateway.payment.domain.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionResponse> businessException(ValidationException exception, HttpServletRequest request) {
        String error = "Validation error";
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ExceptionResponse> resourceNotFound(ObjectNotFoundException exception, HttpServletRequest request) {
        String error = "Not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> badRequest(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String error = "Validation error";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> badRequest(IllegalArgumentException exception, HttpServletRequest request) {
        String error = "Bad request";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ExceptionResponse> jwtError(TokenExpiredException exception, HttpServletRequest request) {
        String error = "Access denied";
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }

    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<ExceptionResponse> jwtError(JWTDecodeException exception, HttpServletRequest request) {
        String error = "Access denied";
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }

    @ExceptionHandler(SignatureVerificationException.class)
    public ResponseEntity<ExceptionResponse> InvalidJwt(SignatureVerificationException exception, HttpServletRequest request) {
        String error = "Access denied";
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> badCredentialsError(BadCredentialsException exception, HttpServletRequest request) {
        String error = "Invalid credentials";
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> accessDeniedError(AccessDeniedException exception, HttpServletRequest request) {
        String error = "Access denied";
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> authenticationError(AuthenticationException exception, HttpServletRequest request) {
        String error = "Authentication failure";
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionResponse> userNotFound(UsernameNotFoundException exception, HttpServletRequest request) {
        String error = "Not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> databaseError(Exception exception, HttpServletRequest request) {
        String error = "Internal server error";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }
    
    private ExceptionResponse errors(HttpStatus status, String error, Exception message, HttpServletRequest request) {
        return new ExceptionResponse(Instant.now(), status.value(), error, message.getMessage(), request.getRequestURI());
    }
}