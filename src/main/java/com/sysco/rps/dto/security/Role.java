package com.sysco.rps.dto.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Role {

  private String code;
  private String name;
  private List<Permission> permissions;

  public Role() {
    this.permissions = new ArrayList<>();
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Permission> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<Permission> permissions) {
    this.permissions = permissions;
  }

  @Override
  public String toString() {
    return "Role{" +
        "code='" + code + '\'' +
        ", name='" + name + '\'' +
        ", permissions=" + permissions +
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
    Role role = (Role) o;
    return code.equals(role.code) &&
        name.equals(role.name) &&
        Objects.equals(permissions, role.permissions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, name, permissions);
  }
}
