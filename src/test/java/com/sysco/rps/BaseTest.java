package com.sysco.rps;

import com.sysco.rps.repository.common.RoutingConnectionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 13. Jul 2020 18:21
 */
@SpringBootTest
@EnableConfigurationProperties
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
public abstract class BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    @Autowired
    RoutingConnectionFactory routingConnectionFactory;

    @BeforeAll
    void updateDBs() {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource[] scripts = new Resource[]{resourceLoader.getResource("classpath:schema.sql"),
              resourceLoader.getResource("classpath:data.sql")};

        new ResourceDatabasePopulator(scripts).execute(routingConnectionFactory)
              .doOnError(e -> logger.error("Failed to load initial data", e))
              .doOnSuccess(s -> logger.info("Successfully loaded initial data"))
              .block();
    }
}
