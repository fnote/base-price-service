package com.sysco.payplus.config;

import com.sysco.payplus.entity.security.Authority;
import com.sysco.payplus.filter.security.JWTAuthenticationFilter;
import com.sysco.payplus.filter.security.JWTAuthorizationFilter;
import com.sysco.payplus.service.security.IntrospectRestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/13/20 Time: 12:54 PM
 */

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  @Qualifier("applicationUserService")
  UserDetailsService userDetailsService;

  @Autowired
  IntrospectRestClientService introspectRestClientService;

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    //have user details service
    auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());

  }

  // Secure the endpoints with authentication and authorization
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().csrf().disable().authorizeRequests()
        .antMatchers(HttpMethod.POST, "/payplus/v1/security/sign-up").permitAll()
        .antMatchers(HttpMethod.GET,"/payplus/v1/health/**").permitAll()
        .antMatchers("/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**").permitAll()
        .antMatchers(HttpMethod.GET, "/*/v1/master-data/opcos/*/drivers/*").hasAnyRole(Authority.ADMIN.toString(), Authority.USER.toString())
        .antMatchers(HttpMethod.GET, "/*/v1/master-data/opcos/*/drivers").hasAnyRole(Authority.ADMIN.toString(), Authority.USER.toString())
        .antMatchers(HttpMethod.POST, "/*/v1/master-data/opcos/*/driver/**").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.POST, "/*/v1/master-data/opcos/*/drivers/**").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.PUT, "/*/v1/master-data/opcos/*/drivers/*/pay-config").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.GET, "/*/v1/master-data/opcos/*/calendar/*").hasAnyRole(Authority.ADMIN.toString(), Authority.PREP.toString())
        .antMatchers(HttpMethod.GET, "/*/v1/master-data/opcos/*").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.GET, "/*/v1/master-data/opcos*").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.POST, "/*/v1/master-data/opco").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.PUT, "/*/v1/master-data/opcos/*").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.GET, "/*/v1/master-data/opcos/*/delay-types")
        .hasAnyRole(Authority.ADMIN.toString(), Authority.PREP.toString(), Authority.APPROVER.toString(), Authority.HR.toString())
        .antMatchers(HttpMethod.POST, "/*/v1/master-data/opcos/*/delay-type").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.PUT, "/*/v1/master-data/opcos/*/delay-types/*").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.GET, "/*/v1/master-data/opcos/*/customers/*").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.GET, "/*/v1/master-data/opcos/*/customers").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.POST, "/*/v1/master-data/opcos/*/customer").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.PATCH, "/*/v1/master-data/opcos/*/customers/*").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.GET, "/*/v1/exceptions/opcos/*/data-exceptions").hasAnyRole(Authority.ADMIN.toString(), Authority.PREP.toString())
        .antMatchers(HttpMethod.PUT, "/*/v1/exceptions/opcos/*/data-exception").hasAnyRole(Authority.ADMIN.toString(), Authority.PREP.toString())
        .antMatchers(HttpMethod.GET, "/*/v1/pay-calculation/**/*").hasAnyRole(Authority.ADMIN.toString(), Authority.PREP.toString(), Authority.APPROVER.toString(), Authority.REPORT_ACCESS.toString())
        .antMatchers(HttpMethod.GET, "/*/v1/master-data/opcos/*/mileage-band-collections/*").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.GET, "/*/v1/master-data/opcos/*/mileage-band-collections").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.POST, "/*/v1/master-data/opcos/*/mileage-band-collection").hasAnyRole(Authority.ADMIN.toString())
        .antMatchers(HttpMethod.PUT, "/*/v1/master-data/opcos/*/mileage-band-collections/*").hasAnyRole(Authority.ADMIN.toString())
        .anyRequest().authenticated()
        .and()
        .addFilter(new JWTAuthenticationFilter(authenticationManager()))
        .addFilter(new JWTAuthorizationFilter(authenticationManager(), introspectRestClientService))
        // this disables session creation on Spring Security
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
    return new SecurityEvaluationContextExtension();
  }

}
