package com.sysco.rps.config;

import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

  private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
  private static final Pattern BREAKING_PATTERN = Pattern.compile("[\n|\r|\t]");
  private static final String REPLACE_STRING = "_";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String method = replaceBreakingPattern(request.getMethod());
    String uriPath = replaceBreakingPattern(request.getRequestURI());
    logger.info("request received: method - {}, path - {}", method, uriPath);
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    String method = replaceBreakingPattern(request.getMethod());
    String uriPath = replaceBreakingPattern(request.getRequestURI());
    logger.info("request completed: method - {}, path - {}", method, uriPath);
  }

  private String replaceBreakingPattern(String input) {
    return BREAKING_PATTERN.matcher(input).replaceAll(REPLACE_STRING);
  }
}
