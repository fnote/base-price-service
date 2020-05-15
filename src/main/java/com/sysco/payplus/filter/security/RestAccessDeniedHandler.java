package com.sysco.payplus.filter.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.payplus.dto.ErrorDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e)
      throws IOException {
    httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
    ErrorDTO errorDTO = new ErrorDTO("2010", "Authorization Failure");
    OutputStream out = httpServletResponse.getOutputStream();
    ObjectMapper mapper = new ObjectMapper();
    httpServletResponse.getOutputStream().println(mapper.writeValueAsString(errorDTO));
    out.flush();
  }
}
