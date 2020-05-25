package com.sysco.payplus.filter.security;

import static com.sysco.payplus.filter.security.SecurityConstants.HEADER_STRING;
import static com.sysco.payplus.filter.security.SecurityConstants.ROLE_PREFIX;
import static com.sysco.payplus.filter.security.SecurityConstants.TOKEN_PREFIX;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.sysco.payplus.dto.security.IntrospectionResponse;
import com.sysco.payplus.dto.security.Role;
import com.sysco.payplus.dto.security.UserDetails;
import com.sysco.payplus.entity.security.Authority;
import com.sysco.payplus.entity.security.UsernameOpCoNumberAuthenticationToken;
import com.sysco.payplus.service.security.ApplicationUserService;
import com.sysco.payplus.service.security.IntrospectRestClientService;
import com.sysco.payplus.util.AuthorizationUtil;
import com.sysco.payplus.util.RequestUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.RestClientException;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/15/20 Time: 12:54 PM
 */

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

  private static final Logger authLogger = LoggerFactory.getLogger(JWTAuthorizationFilter.class);
  @Autowired
  ApplicationUserService applicationUserService;
  private IntrospectRestClientService introspectRestClientService;

  public JWTAuthorizationFilter(AuthenticationManager authManager, IntrospectRestClientService introspectRestClientService) {
    super(authManager);
    this.introspectRestClientService = introspectRestClientService;

  }

  @Override
  protected void doFilterInternal(HttpServletRequest req,
      HttpServletResponse res,
      FilterChain chain) throws IOException, ServletException {
    String header = req.getHeader(HEADER_STRING);

    if (header == null || !header.startsWith(TOKEN_PREFIX)) {
      chain.doFilter(req, res);
      return;
    }

    UsernamePasswordAuthenticationToken authentication = getAuthenticationTokenFromRequest(req);

    SecurityContextHolder.getContext().setAuthentication(authentication);
    chain.doFilter(req, res);
  }

  // TODO: avoid returning null. Throw exceptions
  // TODO: add exception to global exception handler
  private UsernameOpCoNumberAuthenticationToken getAuthenticationTokenFromRequest(HttpServletRequest request) {
    String tokenHeader = request.getHeader(HEADER_STRING);
    if (RequestUtil.isAuthorizationHeaderValid(tokenHeader)) {
      String token = tokenHeader.split(" ")[1];
      try {
        return getAuthorizedTokenWithRolesAndOpco(RequestUtil.getOpcoNumberFromRequest(request), token);
      } catch (AccessDeniedException e) {
        authLogger.error("Access is denied", e);
        return null;
      }
    }
    return null;
  }

  private UsernameOpCoNumberAuthenticationToken getAuthorizedTokenWithRolesAndOpco(String providedOpcoNumber, String token) {
    UserDetails userDetails;
    try {
      IntrospectionResponse introspectionResponse = introspectRestClientService.getUserDataFromIntrospectService(token);
      if (AuthorizationUtil.validateIntrospectionResponse(introspectionResponse)) {
        userDetails = introspectionResponse.getUserDetails();
        String username = userDetails.getUserName();
        ImmutableList<Role> roles = ImmutableList.copyOf(userDetails.getRoles());
        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> {
          String roleName = ROLE_PREFIX + role.getName().toUpperCase();
          authorities.add((GrantedAuthority) () -> roleName);
        });

        ImmutableSet<String> assignedOpcos = ImmutableSet.copyOf(userDetails.getScopes());

        if (AuthorizationUtil.userHasAuthority(ROLE_PREFIX + Authority.ADMIN, authorities) ||
            assignedOpcos.contains(String.valueOf(providedOpcoNumber))) {
          return new UsernameOpCoNumberAuthenticationToken(username, providedOpcoNumber, authorities);
        }

        throw new AccessDeniedException("Trying to access unassigned opco. Access blocked");
      }
      throw new AccessDeniedException("Unable to verify the token");
    } catch (RestClientException e) {
      throw new AccessDeniedException("Unable to verify the token from authorization service");
    }
  }
}