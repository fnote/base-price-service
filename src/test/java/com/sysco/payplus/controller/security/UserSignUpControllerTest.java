package com.sysco.payplus.controller.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.payplus.entity.security.ApplicationUser;
import com.sysco.payplus.service.security.ApplicationUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/16/20
 * Time: 12:54 PM
 */


@WebMvcTest(UserSignUpController.class)
@EnableAutoConfiguration

public class UserSignUpControllerTest {

    private final String API_PATH = "/api-blueprint/v1";
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ApplicationUserService applicationUserService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private UserSignUpController userSignUpController;


    @Test
    public void contexLoads() throws Exception {
        assertThat(userSignUpController).isNotNull();
    }

    @Test
    public void whenSignUp_returnsOK() throws Exception {
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setUsername("rohanaonly");
        applicationUser.setPassword("gvt123");
        String applicationUserJson = new ObjectMapper().writeValueAsString(applicationUser);

        //mock the expected response from the service
        when(applicationUserService.save(applicationUser)).thenReturn(applicationUser);
        mvc.perform(MockMvcRequestBuilders.post(API_PATH + "/sign-up")
                .content(applicationUserJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }
}