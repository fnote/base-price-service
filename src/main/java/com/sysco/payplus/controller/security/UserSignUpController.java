package com.sysco.payplus.controller.security;

import com.sysco.payplus.service.security.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/15/20 Time: 12:54 PM
 */

@RestController
@RequestMapping("/payplus/v1/security")
public class UserSignUpController {

    @Autowired
    private ApplicationUserService applicationUserService;


    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
        //TODO remove if not used
    String signUp() {
        return "{\"success\":true}";
    }

}
