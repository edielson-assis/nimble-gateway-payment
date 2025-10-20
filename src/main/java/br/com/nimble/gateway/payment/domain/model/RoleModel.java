package br.com.nimble.gateway.payment.domain.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(of = "roleId")
@Setter
@Getter
@Entity
@Table(name = "roles")
public class RoleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID roleId;

    @Column(nullable = false, unique = true, length = 30)
    private String roleName;
}