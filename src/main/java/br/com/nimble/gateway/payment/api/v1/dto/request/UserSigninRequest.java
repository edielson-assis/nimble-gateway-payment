package br.com.nimble.gateway.payment.api.v1.dto.request;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class UserSigninRequest implements Serializable {

    private String cpf;
    private String email;
    private String password;
}