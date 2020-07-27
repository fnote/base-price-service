package com.sysco.rps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/13/20 Time: 12:54 PM
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class })

public class ReferencePriceService {

  public static void main(String[] args) {
    SpringApplication.run(ReferencePriceService.class, args);
  }

}
