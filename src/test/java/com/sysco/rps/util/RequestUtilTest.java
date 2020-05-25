package com.sysco.rps.util;


import static com.sysco.rps.filter.security.SecurityConstants.HEADER_STRING;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.sysco.rps.filter.security.JWTAuthorizationFilter;
import com.sysco.rps.service.security.IntrospectRestClientService;
import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;

@RunWith(MockitoJUnitRunner.class)
public class RequestUtilTest {

  private static final String token = "Bearer 260bce87-6be9-4897-add7-b3b675952538";
  private static final String AdminValidOpCoURL = "/payplus/v1/master-data/opcos/12345/Admin/1";
  private static final String AdminInValidOpCoURL = "/payplus/v1/master-data/opcos/TestOpCo/Admin/1";
  private static final String AdminWithoutOpCoURL = "/payplus/v1/master-data/opcos/";

  private MockFilterChain mockChain;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private JWTAuthorizationFilter filterUnderTest;

  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private IntrospectRestClientService introspectRestClientService;

  @Before
  public void initializeBeforeEachTestRun() {
    mockChain = new MockFilterChain();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    filterUnderTest = new JWTAuthorizationFilter(authenticationManager, introspectRestClientService);
  }

  @Test(expected = NumberFormatException.class)
  public void whenAdminInvalidOpCo() throws ServletException, IOException {
    request.addHeader(HEADER_STRING, token);
    request.setRequestURI(AdminInValidOpCoURL);
    filterUnderTest.doFilter(request, response, mockChain);
    assertFalse(throwNumberFormatException());
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void whenURLHasNoOpCo() throws ServletException, IOException {
    request.addHeader(HEADER_STRING, token);
    request.setRequestURI(AdminWithoutOpCoURL);
    filterUnderTest.doFilter(request, response, mockChain);
    assertFalse(throwIndexOutOfBoundException());
  }

  private boolean throwIndexOutOfBoundException() {
    throw new IndexOutOfBoundsException();
  }

  private boolean throwNumberFormatException() {
    throw new NumberFormatException();
  }
}
