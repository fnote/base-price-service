package com.sysco.rps.config;

import com.sysco.rps.service.security.IntrospectRestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/13/20 Time: 12:54 PM
 */

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  IntrospectRestClientService introspectRestClientService;

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    // TODO: consider removing SpringSecurityConfig
  }

  // Secure the endpoints with authentication and authorization
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().csrf().disable().authorizeRequests()
        .antMatchers("*").permitAll()
        .and()
        // this disables session creation on Spring Security
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
