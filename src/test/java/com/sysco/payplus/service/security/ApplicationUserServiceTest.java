package com.sysco.payplus.service.security;

import com.sysco.payplus.entity.security.ApplicationUser;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import com.sysco.payplus.entity.security.Role;

import java.util.ArrayList;
import java.util.List;

@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class ApplicationUserServiceTest {

    @Autowired
    ApplicationUserService applicationUserService;

    @Test
    void whenSaveApplicationUser_returnsApplicationUserWithId() {
        ApplicationUser applicationUser=new ApplicationUser();
        applicationUser.setUsername("rohana");
        applicationUser.setPassword("gvt123");
        ApplicationUser saved= applicationUserService.save(applicationUser);
        assertEquals(applicationUser.getUsername(),saved.getUsername());
        //password must be encrypted
        assertNotEquals("gvt123",saved.getPassword());
    }

    @Test
    void whenFindByUserName_returnsApplicationUser() {
        ApplicationUser applicationUser=new ApplicationUser();
        applicationUser.setUsername("rohana");
        applicationUser.setPassword("gvt123");
        applicationUserService.save(applicationUser);
        ApplicationUser found= applicationUserService.findByUsername(applicationUser.getUsername());
        assertEquals(applicationUser.getUsername(),found.getUsername());

    }


    @Test
    void whenSaveApplicationUserWithRoles_returnsApplicationUserWithIdAndRoles() {
        ApplicationUser applicationUser=new ApplicationUser();
        applicationUser.setUsername("rohana");
        applicationUser.setPassword("gvt123");
        Role r=new Role("ROLE_USER");
        r.setApplicationUser(applicationUser);
        Role r2=new Role("ROLE_ADMIN");
        r2.setApplicationUser(applicationUser);
        List<Role> roles=new ArrayList<>();
        roles.add(r);
        roles.add(r2);
        applicationUser.setApplicationUserRoles(roles);
        applicationUserService.save(applicationUser);

        ApplicationUser fetchedUser=applicationUserService.findByUsername(applicationUser.getUsername());
        assertEquals(2,fetchedUser.getApplicationUserRoles().size());

    }


}