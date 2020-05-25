package com.sysco.rps.filter.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.rps.dto.ErrorDTO;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e)
      throws IOException {

    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    ErrorDTO errorDTO = new ErrorDTO("2000", "Authentication Failure");
    OutputStream out = httpServletResponse.getOutputStream();
    ObjectMapper mapper = new ObjectMapper();
    httpServletResponse.getOutputStream().println(mapper.writeValueAsString(errorDTO));
    out.flush();
  }
}
