package com.sysco.rps.repository.security;

import com.sysco.rps.entity.security.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/15/20 Time: 12:54 PM
 */

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {

    ApplicationUser findByUsername(String username);

}
