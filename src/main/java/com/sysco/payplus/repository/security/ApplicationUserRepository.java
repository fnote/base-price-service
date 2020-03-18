package com.sysco.payplus.repository.security;

import com.sysco.payplus.entity.security.ApplicationUser;
import com.sysco.payplus.entity.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/15/20
 * Time: 12:54 PM
 */

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    ApplicationUser findByUsername(String username);

   // @Query(value = "SELECT r FROM Role r WHERE r.applicationUser= :firstName and u.lastName = :lastName", nativeQuery = false)
   // List<Role> getApplicationUserRoles(String username);

}