package com.sysco.payplus.service.security;

import static java.util.Collections.emptyList;

import com.sysco.payplus.entity.security.ApplicationUser;
import com.sysco.payplus.repository.security.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/16/20 Time: 12:54 PM
 */
@Service
@Qualifier("applicationUserService")
public class ApplicationUserServiceImpl implements ApplicationUserService, UserDetailsService {

  @Autowired
  private ApplicationUserRepository applicationUserRepository;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public ApplicationUser findByUsername(String username) {
    return applicationUserRepository.findByUsername(username);
  }

  @Override
  public ApplicationUser save(ApplicationUser applicationUser) {
    applicationUser.setPassword(bCryptPasswordEncoder.encode(applicationUser.getPassword()));
    return applicationUserRepository.save(applicationUser);
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    ApplicationUser applicationUser = findByUsername(username);
    if (applicationUser == null) {
      throw new UsernameNotFoundException(username);
    }
    return new User(applicationUser.getUsername(), applicationUser.getPassword(), emptyList());

  }
}
