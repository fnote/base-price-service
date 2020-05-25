package com.sysco.payplus.filter.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.sysco.payplus.filter.security.SecurityConstants.EXPIRATION_TIME;
import static com.sysco.payplus.filter.security.SecurityConstants.SECRET;
import static com.sysco.payplus.filter.security.SecurityConstants.TOKEN_PREFIX;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.payplus.entity.security.ApplicationUser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/15/20 Time: 12:54 PM
 */

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;


    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /*
    Get called in order to authenticate the request. we delegate that to authentication manager.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
        HttpServletResponse res) {
        try {
            ApplicationUser creds = new ObjectMapper()
                .readValue(req.getInputStream(), ApplicationUser.class);

            return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    creds.getUsername(),
                    creds.getPassword(),
                    new ArrayList<>())
            );
        } catch (IOException e) {
            //TODO Fix this
            throw new RuntimeException(e);
        }
    }

    /*
     * This gets called when the user successfully log in using the credentials. So we can generate the JWT token.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
        HttpServletResponse res,
        FilterChain chain,
        Authentication auth) throws IOException, ServletException {

        String token = JWT.create()
            .withSubject(((User) auth.getPrincipal()).getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
        res.addHeader(SecurityConstants.HEADER_STRING, TOKEN_PREFIX + token);
    }
}
