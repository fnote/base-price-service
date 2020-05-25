package com.sysco.rps.dto.security;

import java.util.Objects;

// TODO: Take the decision on how to handle authorization entities in mysql or postgress and do proper placement
public class Permission {

  private String code;
  private String name;

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

  @Override
  public String toString() {
    return "Permission{" +
        "code='" + code + '\'' +
        ", name='" + name + '\'' +
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
    Permission that = (Permission) o;
    return code.equals(that.code) &&
        name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, name);
  }
}
