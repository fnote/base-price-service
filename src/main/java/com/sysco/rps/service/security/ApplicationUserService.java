package com.sysco.rps.service.security;

import com.sysco.rps.entity.security.ApplicationUser;

public interface ApplicationUserService {

  ApplicationUser findByUsername(String username);

  ApplicationUser save(ApplicationUser applicationUser);

}
