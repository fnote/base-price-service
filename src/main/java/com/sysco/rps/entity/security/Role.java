package com.sysco.rps.entity.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/16/20 Time: 12:54 PM
 */

@Entity
@Table(name = "application_user_role")
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Column(name = "role_name", length = 45, nullable = false, unique = false)
  @NotBlank(message = "Role name is mandatory")
  private String roleName;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "application_user_id", nullable = false)
  private ApplicationUser applicationUser;

  public Role() {

  }

  public Role(String roleName) {
    this.roleName = roleName;
  }

  public ApplicationUser getApplicationUser() {
    return applicationUser;
  }

  public void setApplicationUser(ApplicationUser applicationUser) {
    this.applicationUser = applicationUser;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String role) {
    this.roleName = role;
  }

  public GrantedAuthority toGrantedAuthority() {
    return () -> roleName;
  }

}
