package com.sysco.rps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import java.util.Collections;

/**
 * Webflux Swagger Configs
 *
 * @author rohana.kumara@sysco.com
 * @authoer Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @end Created : 3/13/20 Time: 12:54 PM
 */

@Configuration
@EnableSwagger2WebFlux
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
              .useDefaultResponseMessages(false)
              .select()
              .apis(RequestHandlerSelectors.any())
              .paths(PathSelectors.regex("/health.*|/error.*|/ref-price/v1.*").negate())
              .build()
              .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
              "Reference Pricing Service",
              "APIs to expose reference pricing",
              "v1",
              "Terms of service",
              new Contact("Cloud Pricing Team", "www.sysco.com", "btspricing@Sysco.com"),
              "License of API", "API license URL", Collections.emptyList());
    }
}
