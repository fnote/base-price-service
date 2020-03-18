package com.sysco.payplus.service.security;

import com.sysco.payplus.entity.security.ApplicationUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ApplicationUserService {
    ApplicationUser findByUsername(String username);
    ApplicationUser save(ApplicationUser applicationUser);

}
