package com.sysco.payplus.config;

import com.sysco.payplus.filter.security.JWTAuthenticationFilter;
import com.sysco.payplus.filter.security.JWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/13/20
 * Time: 12:54 PM
 */

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        //have user details service
        auth.userDetailsService(userDetailsService).passwordEncoder( bCryptPasswordEncoder);
        /*auth.authenticationProvider(new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                GrantedAuthority ga = new GrantedAuthority() {
                    @Override
                    public String getAuthority() {
                        return "ROLE_USER";
                    }
                };
                List l = new ArrayList();
                l.add(ga);
                if (authentication.getPrincipal().equals("rohana") && authentication.getCredentials().equals("secret")) {
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), "notneeded", l);
                    return token;
                }
                throw new BadCredentialsException("Authentication failure for " + authentication.getPrincipal());
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return true;
            }
        });*/
    }

    // Secure the endpoints with authentication and authorization
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
               .antMatchers(HttpMethod.POST, "/*/*/sign-up").permitAll()
                //Actual role must be ROLE_USER. This is spring thing
               .antMatchers(HttpMethod.GET,"/*/*/book**").hasAnyRole("USER","ADMIN")
               .antMatchers(HttpMethod.POST,"/*/*/book**").hasRole("ADMIN")
               .anyRequest().authenticated()
               .and()
               .addFilter(new JWTAuthenticationFilter(authenticationManager()))
               .addFilter(new JWTAuthorizationFilter(authenticationManager()))
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
