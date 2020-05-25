package com.sysco.payplus.controller.security;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sysco.payplus.service.security.ApplicationUserService;
import com.sysco.payplus.service.security.IntrospectRestClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/16/20 Time: 12:54 PM
 */

//looks like we need a controller class always.
@WebMvcTest(UserSignUpController.class)
@EnableAutoConfiguration
/**
 * Note that /login does not have a explicit controller. This is enabled by spring security.
 */
public class UserLoginControllerTest {

  private final String API_PATH = "/api-blueprint/v1";
  @MockBean
  IntrospectRestClientService introspectRestClientService;
  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;
  @Autowired
  private MockMvc mvc;
  @MockBean
  @Qualifier("applicationUserService")
  private UserDetailsService userDetailsService;
  @MockBean
  private ApplicationUserService applicationUserService;

  @Test
  public void whenLoginWithCorrectCreds_returnsExpectedStatusCodes() throws Exception {
    String username = "rohanaonly";
    String password = "gvt123";
    String applicationUserJson = "{ \"username\": \"" + username + "\",\"password\": \"" + password + "\" }";
    //mock the expected response from the service
    when(userDetailsService.loadUserByUsername(username)).thenReturn(new User(username, bCryptPasswordEncoder.encode(password), emptyList()));
    ResultMatcher accessHeader = MockMvcResultMatchers.header().exists("Authorization");
    mvc.perform(MockMvcRequestBuilders.post("/login")
        .content(applicationUserJson)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(accessHeader)
        .andDo(new ResultHandler() {
          @Override
          public void handle(MvcResult result) throws Exception {
            result.getRequest().getHeader("Authorization");
            //do a jwt based authentication.
            result.getRequest().getHeader("Authorization");

                        /*mvc.perform(MockMvcRequestBuilders.post("/login")
                                .content(applicationUserJson)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());*/

          }
        })
        .andDo(print());
  }


  @Test
  public void whenLoginWithIncorrectCreds_returnsExpectedStatusCodes() throws Exception {
    String username = "rohanaonly";
    String password = "gvt123";
    String applicationUserJson = "{ \"username\": \"" + username + "\",\"password\": \"" + password + "\" }";
    when(userDetailsService.loadUserByUsername(username)).thenThrow(new UsernameNotFoundException(username));
    mvc.perform(MockMvcRequestBuilders.post("/login")
        .content(applicationUserJson)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }

  @Test
  public void whenNonAdminSendOpCoNumber_thenAccessDenide() throws Exception {
    String username = "rohanaonly";
    String password = "gvt123";
    String applicationUserJson = "{ \"username\": \"" + username + "\",\"password\": \"" + password + "\" }";
    //simulate successful login
    when(userDetailsService.loadUserByUsername(username)).thenReturn(new User(username, bCryptPasswordEncoder.encode(password), emptyList()));
    mvc.perform(MockMvcRequestBuilders.post("/api-blueprint/v1/signup")
        .param("opco", "1")
        .content(applicationUserJson)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(print());
  }


}