package com.sysco.rps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * Main class to bootstrap the app
 *
 * @author Rohana Kumara
 * @copyright (C) 2020, Sysco Corporation
 * @end Created : 3/13/20 Time: 12:54 PM
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableR2dbcRepositories
public class ReferencePriceService {

    public static void main(String[] args) {
        SpringApplication.run(ReferencePriceService.class, args);
    }

}
