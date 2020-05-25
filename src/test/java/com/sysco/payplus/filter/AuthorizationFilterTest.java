package com.sysco.payplus.filter;

import static com.sysco.payplus.filter.security.SecurityConstants.HEADER_STRING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.sysco.payplus.dto.security.IntrospectionResponse;
import com.sysco.payplus.dto.security.Role;
import com.sysco.payplus.dto.security.UserDetails;
import com.sysco.payplus.entity.security.Authority;
import com.sysco.payplus.filter.security.JWTAuthorizationFilter;
import com.sysco.payplus.service.security.IntrospectRestClientService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestClientException;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationFilterTest {

  private static final String token = "Bearer 260bce87-6be9-4897-add7-b3b675952538";
  private static final String AdminOpCoOneURL = "/payplus/v1/master-data/opcos/12345/Admin/1";
  private static final String AdminOpCoTwoURL = "/payplus/v1/master-data/opcos/12345/Admin/2";
  private static final String UserURL = "/payplus/v1/master-data/opcos/12345/drivers/1";

  private MockFilterChain mockChain;
  private MockHttpServletRequest request;
  private IntrospectionResponse introspectionResponse;
  private UserDetails userDetails;
  private MockHttpServletResponse response;
  private Role role;
  private List<Role> roles;
  private JWTAuthorizationFilter filterUnderTest;

  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private IntrospectRestClientService introspectRestClientService;

  @Before
  public void initializeBeforeEachTestRun() {
    mockChain = new MockFilterChain();
    request = new MockHttpServletRequest();
    introspectionResponse = new IntrospectionResponse();
    userDetails = new UserDetails();
    response = new MockHttpServletResponse();
    role = new Role();
    roles = new ArrayList<>();
    filterUnderTest = new JWTAuthorizationFilter(authenticationManager, introspectRestClientService);
  }


  @Test
  public void whenAdminAccessWithNullToken() throws ServletException, IOException {
    introspectionResponse.setActive(true);
    userDetails.setUserName("test_admin");
    role.setCode("1");
    role.setName(Authority.ADMIN.toString());
    roles.add(role);
    userDetails.setRoles(roles);
    request.addHeader(HEADER_STRING, "Bearer ");
    request.setRequestURI(AdminOpCoOneURL);
    filterUnderTest.doFilter(request, response, mockChain);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void whenUserAccessUnassignedOpCo() throws ServletException, IOException {
    introspectionResponse.setActive(true);
    userDetails.setUserName("test_user");
    Set<String> scopes = new HashSet<>();
    scopes.add("123456");
    userDetails.setScopes(scopes);
    role.setCode("1");
    role.setName(Authority.USER.toString());
    roles.add(role);
    userDetails.setRoles(roles);
    introspectionResponse.setUserDetails(userDetails);
    Mockito.when(introspectRestClientService.getUserDataFromIntrospectService(token.split(" ")[1])).thenReturn(introspectionResponse);
    request.addHeader(HEADER_STRING, token);
    request.setRequestURI(UserURL);
    filterUnderTest.doFilter(request, response, mockChain);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void whenAdminTriesToAccessOpCoOne() throws ServletException, IOException {
    introspectionResponse.setActive(true);
    userDetails.setUserName("test_user");
    Set<String> scopes = new HashSet<>();
    scopes.add("12345");
    userDetails.setScopes(scopes);
    role.setCode("1");
    role.setName(Authority.USER.toString());
    roles.add(role);
    userDetails.setRoles(roles);
    introspectionResponse.setUserDetails(userDetails);
    Mockito.when(introspectRestClientService.getUserDataFromIntrospectService(token.split(" ")[1])).thenReturn(introspectionResponse);
    request.addHeader(HEADER_STRING, token);
    request.setRequestURI(UserURL);
    filterUnderTest.doFilter(request, response, mockChain);
    assertEquals(1, SecurityContextHolder.getContext().getAuthentication().getAuthorities().size());
  }

  @Test
  public void whenAdminTriesToAccessOpCoTwo() throws ServletException, IOException {
    userDetails.setUserName("test_admin");
    role.setCode("1");
    role.setName(Authority.ADMIN.toString());
    roles.add(role);
    userDetails.setRoles(roles);
    introspectionResponse.setUserDetails(userDetails);
    Mockito.when(introspectRestClientService.getUserDataFromIntrospectService(token.split(" ")[1])).thenReturn(introspectionResponse);
    request.addHeader(HEADER_STRING, token);
    request.setRequestURI(AdminOpCoTwoURL);
    filterUnderTest.doFilter(request, response, mockChain);
    assertEquals(1, SecurityContextHolder.getContext().getAuthentication().getAuthorities().size());
  }

  @Test
  public void whenAdminAccessOpCoOne() throws ServletException, IOException {
    introspectionResponse.setActive(true);
    userDetails.setUserName("test_admin");
    role.setCode("1");
    role.setName(Authority.ADMIN.toString());
    roles.add(role);
    userDetails.setRoles(roles);
    introspectionResponse.setUserDetails(userDetails);
    Mockito.when(introspectRestClientService.getUserDataFromIntrospectService(token.split(" ")[1])).thenReturn(introspectionResponse);
    request.addHeader(HEADER_STRING, token);
    request.setRequestURI(AdminOpCoOneURL);
    filterUnderTest.doFilter(request, response, mockChain);
    assertEquals(1, SecurityContextHolder.getContext().getAuthentication().getAuthorities().size());
  }

  @Test
  public void whenAdminAccessWithInvalidToken() throws ServletException, IOException {
    introspectionResponse.setActive(true);
    userDetails.setUserName("test_admin");
    Role role = new Role();
    role.setCode("1");
    role.setName(Authority.ADMIN.toString());
    roles.add(role);
    userDetails.setRoles(roles);
    Mockito.when(introspectRestClientService.getUserDataFromIntrospectService(token.split(" ")[1])).thenReturn(introspectionResponse);
    request.addHeader(HEADER_STRING, token);
    request.setRequestURI(AdminOpCoOneURL);
    filterUnderTest.doFilter(request, response, mockChain);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void whenAuthorizationServiceIsDown() throws ServletException, IOException {
    introspectionResponse.setActive(true);
    userDetails.setUserName("test_admin");
    role.setCode("1");
    role.setName(Authority.ADMIN.toString());
    roles.add(role);
    userDetails.setRoles(roles);
    introspectionResponse.setUserDetails(userDetails);
    Mockito.when(introspectRestClientService.getUserDataFromIntrospectService(token.split(" ")[1]))
        .thenThrow(new RestClientException("Unable to verify the token from authorization service"));
    request.addHeader(HEADER_STRING, token);
    request.setRequestURI(AdminOpCoOneURL);
    filterUnderTest.doFilter(request, response, mockChain);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

}