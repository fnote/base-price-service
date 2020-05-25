package com.sysco.rps.dto.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IntrospectionResponse {

  private boolean active;
  @JsonProperty("data")
  private UserDetails userDetails;

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public UserDetails getUserDetails() {
    return userDetails;
  }

  public void setUserDetails(UserDetails userDetails) {
    this.userDetails = userDetails;
  }

  @Override
  public String toString() {
    return "IntrospectionResponse{" +
        "active=" + active +
        ", userDetails=" + userDetails +
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
    IntrospectionResponse that = (IntrospectionResponse) o;
    return active == that.active &&
        Objects.equals(userDetails, that.userDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(active, userDetails);
  }
}
