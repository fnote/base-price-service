package com.sysco.payplus.service.security;

import com.sysco.payplus.entity.security.ApplicationUser;

public interface ApplicationUserService {

  ApplicationUser findByUsername(String username);

  ApplicationUser save(ApplicationUser applicationUser);

}
