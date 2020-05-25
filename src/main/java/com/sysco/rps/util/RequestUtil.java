package com.sysco.rps.util;

import javax.servlet.http.HttpServletRequest;

// TODO: Have a common util class
// TODO: Throw proper exceptions on validation failure
public class RequestUtil {

  private RequestUtil() {
  }

  public static boolean isAuthorizationHeaderValid(String authorizationHeader) {
    return authorizationHeader != null && authorizationHeader.split(" ").length == 2;
  }

  public static String getOpcoNumberFromRequest(HttpServletRequest request) {
    try {
      return request.getRequestURI().split("/")[5];
    } catch (IndexOutOfBoundsException e) {
//      throw new AccessDeniedException("Unable to capture opco number from request");
      //TODO: exception is commented as GET Opcos URL doesn't have OpCo Number
      return null;
    }
  }
}
