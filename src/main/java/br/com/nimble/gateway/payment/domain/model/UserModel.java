package br.com.nimble.gateway.payment.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.nimble.gateway.payment.domain.model.enums.UserStatus;
import br.com.nimble.gateway.payment.domain.model.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(of = "userId")
@Getter
@Setter
@Entity
@Table(name = "users")
public class UserModel {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID userId;

  @Column(nullable = false, length = 150)
  private String fullName;

  @Column(nullable = false, unique = true, length = 14)
  private String cpf;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(nullable = false, length = 255)
  private String password;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserStatus userStatus;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserType userType;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_role", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id") })
  private final List<RoleModel> permissions = new ArrayList<>();

  public List<String> getRoles() {
    return permissions.stream().map(RoleModel::getRoleName).collect(Collectors.toList());
  }
}