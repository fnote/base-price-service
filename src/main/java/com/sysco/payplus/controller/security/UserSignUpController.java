package com.sysco.payplus.controller.security;

import com.sysco.payplus.entity.security.ApplicationUser;
import com.sysco.payplus.service.security.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/15/20
 * Time: 12:54 PM
 */

@RestController
@RequestMapping("/api-blueprint")
public class UserSignUpController {

    @Autowired
    private ApplicationUserService applicationUserService;


    @PostMapping("v1/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public  @ResponseBody
    String signUp(@RequestBody ApplicationUser user) {
        applicationUserService.save(user);
        return "{\"success\":true}";
    }

}
