package com.sysco.rps.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.SSL;

/**
 * Common DB Client Configuration.
 * Initializes a Database Client for connecting to Common DB.
 *
 * @author Tharuka Jayalath
 * (C) 2021, Sysco Corporation
 * Created: 3/18/21. Thu 14:00
 */
@Profile("!test")
@Component
public class CommonDBClientConfig {

    private static final String COMMON_DB_NAME = "METADATA";

    private final DatabaseClient databaseClient;

    public CommonDBClientConfig(@Value("${common.db.endpoint}") String commonDBEndpoint,
                                @Value("${pricing.db.username}") String username,
                                @Value("${pricing.db.password}") String password) {

        this.databaseClient = DatabaseClient.create(
                ConnectionFactories.get(
                        ConnectionFactoryOptions.builder()
                                .option(DRIVER, "mysql")
                                .option(HOST, commonDBEndpoint)
                                .option(USER, username)
                                .option(PASSWORD, password)
                                .option(DATABASE, COMMON_DB_NAME)
                                .option(SSL, false)
                                .build()
                )
        );
    }

    public DatabaseClient getDatabaseClient() {
        return databaseClient;
    }

}
