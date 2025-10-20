package br.com.nimble.gateway.payment.api.v1.dto.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse implements Serializable {

    private String fullName;
    private String cpf;
    private String email;
}