package com.sysco.rps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Adds hooks for all operators so that MDCContextLifter is used on each
 *
 * @author rohana.kumara@sysco.com
 * @copyright (C) 2020, Sysco Corporation
 * @end Created : 3/28/20 Time: 12:54 PM
 */

@Configuration
@EnableAsync
public class AppConfig implements WebFluxConfigurer {

    @Bean
    public ResourceBundleMessageSource messageSource() {

        var source = new ResourceBundleMessageSource();
        source.setBasenames("errors");
        source.setUseCodeAsDefaultMessage(true);

        return source;
    }
}
