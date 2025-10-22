package br.com.nimble.gateway.payment.api.v1.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.nimble.gateway.payment.api.v1.doc.UserControllerDocs;
import br.com.nimble.gateway.payment.api.v1.dto.request.UserPutPasswordRequest;
import br.com.nimble.gateway.payment.api.v1.dto.request.UserPutRequest;
import br.com.nimble.gateway.payment.api.v1.dto.response.UserResponse;
import br.com.nimble.gateway.payment.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerDocs {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
			@RequestParam(defaultValue = "0") Integer page, 
			@RequestParam(defaultValue = "10") Integer size, 
			@RequestParam(defaultValue = "asc") String direction) {
        var users = userService.findAllUsers(page, size, direction);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getOneUser(@PathVariable UUID userId) {
        var user = userService.findUser(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @RequestBody @Valid UserPutRequest userDto) {
        var user = userService.updateUser(userId, userDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(
            @RequestBody @Valid UserPutPasswordRequest userDto) {
        var user = userService.updateUserPassword(userDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

	@DeleteMapping(path = "/{userId}")
	public ResponseEntity<Void> disableUser(@PathVariable UUID userId) {
		userService.disableUser(userId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}