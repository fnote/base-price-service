package com.sysco.payplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/13/20 Time: 12:54 PM
 */
@SpringBootApplication
@EnableGlobalMethodSecurity(
    prePostEnabled = true)

public class PlauPlus {

  public static void main(String[] args) {
    SpringApplication.run(PlauPlus.class, args);
  }

}
