package com.sysco.rps.util;

// TODO: Have a common util class
// TODO: Throw proper exceptions on validation failure
public class RequestUtil {

  private RequestUtil() {
  }

  public static boolean isAuthorizationHeaderValid(String authorizationHeader) {
    return authorizationHeader != null && authorizationHeader.split(" ").length == 2;
  }

}
