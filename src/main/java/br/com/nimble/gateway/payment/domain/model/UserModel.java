package br.com.nimble.gateway.payment.domain.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class UserModel implements UserDetails {

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

  @Column(nullable = false, name = "is_account_non_expired")
  private boolean accountNonExpired = true;

  @Column(nullable = false, name = "is_account_non_locked")
  private boolean accountNonLocked = true;

  @Column(nullable = false, name = "is_credentials_non_expired")
  private boolean credentialsNonExpired = true;

  @Column(nullable = false, name = "is_enabled")
  private boolean enabled = true;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_role", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id") })
  private final List<RoleModel> permissions = new ArrayList<>();

  public List<String> getRoles() {
    return permissions.stream().map(RoleModel::getRoleName).collect(Collectors.toList());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.permissions;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }
}