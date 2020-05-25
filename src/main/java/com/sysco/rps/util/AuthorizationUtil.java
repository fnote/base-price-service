package com.sysco.rps.util;

import com.sysco.rps.dto.security.IntrospectionResponse;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;

// TODO: Have a common util class
// TODO: Throw proper exceptions on validation failure
public class AuthorizationUtil {

  private AuthorizationUtil() {
  }

  public static boolean validateIntrospectionResponse(IntrospectionResponse introspectionResponse) {
    return introspectionResponse != null && introspectionResponse.getUserDetails() != null;
  }

  public static boolean userHasAuthority(String authority, List<GrantedAuthority> authorities) {
    return authorities.stream().anyMatch(grantedAuthority -> authority.equals(grantedAuthority.getAuthority()));
  }
}
