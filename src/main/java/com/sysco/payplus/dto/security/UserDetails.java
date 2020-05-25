package com.sysco.payplus.dto.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class UserDetails {

  private String networkId;
  private String userName;
  private String email;
  private Set<String> scopes;
  private List<Role> roles;
  private List<String> vendors;

  public UserDetails() {
    this.scopes = new HashSet<>();
    this.roles = new ArrayList<>();
    this.vendors = new ArrayList<>();
  }

  public String getNetworkId() {
    return networkId;
  }

  public void setNetworkId(String networkId) {
    this.networkId = networkId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Set<String> getScopes() {
    return scopes;
  }

  public void setScopes(Set<String> scopes) {
    this.scopes = scopes;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }

  public List<String> getVendors() {
    return vendors;
  }

  public void setVendors(List<String> vendors) {
    this.vendors = vendors;
  }

  @Override
  public String toString() {
    return "UserDetails{" +
        "networkId='" + networkId + '\'' +
        ", userName='" + userName + '\'' +
        ", email='" + email + '\'' +
        ", scopes=" + scopes +
        ", roles=" + roles +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDetails that = (UserDetails) o;
    return Objects.equals(networkId, that.networkId) &&
        Objects.equals(userName, that.userName) &&
        Objects.equals(email, that.email) &&
        Objects.equals(scopes, that.scopes) &&
        Objects.equals(roles, that.roles) &&
        Objects.equals(vendors, that.vendors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(networkId, userName, email, scopes, roles, vendors);
  }
}
