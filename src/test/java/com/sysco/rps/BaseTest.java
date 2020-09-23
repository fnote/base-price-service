package com.sysco.rps;

import com.sysco.pricing.junitqcenterintegrator.TestResultsLogger;
import com.sysco.rps.repository.common.RoutingConnectionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;

import static com.sysco.rps.misc.TestConstants.FEATURE_NAME;
import static com.sysco.rps.misc.TestConstants.FILE_PATH;
import static com.sysco.rps.misc.TestConstants.NODE_NAME;
import static com.sysco.rps.misc.TestConstants.TEST_ENV;
import static com.sysco.rps.misc.TestConstants.TEST_PROJECT;
import static com.sysco.rps.misc.TestConstants.TEST_RELEASE;
import static com.sysco.rps.misc.TestConstants.UPDATE_DASHBOARD;
import static com.sysco.rps.misc.TestConstants.WRITE_TO_FILE;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 13. Jul 2020 18:21
 */
@SpringBootTest
@EnableConfigurationProperties
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    @RegisterExtension
    TestResultsLogger testResultsLogger =
          new TestResultsLogger.Builder(TEST_ENV, TEST_RELEASE, TEST_PROJECT, FEATURE_NAME,
                UPDATE_DASHBOARD)
                .setNode(NODE_NAME)
                .setWriteToFile(WRITE_TO_FILE)
                .setFilePath(FILE_PATH)
                .build();

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
