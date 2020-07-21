package com.sysco.rps.config;

import com.sysco.rps.repository.common.RoutingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.DatabaseClient;

/**
 * Configuration class for database client
 *
 * @author Tharuka Jayalath
 * (C) 2020, Sysco Corporation
 * Created: 7/6/20. Mon 2020 09:53
 */
@Configuration
public class DatabaseClientConfig {

    private RoutingConnectionFactory routingConnectionFactory;

    @Autowired
    public DatabaseClientConfig(RoutingConnectionFactory routingConnectionFactory) {
        this.routingConnectionFactory = routingConnectionFactory;
    }

    @Bean
    public DatabaseClient databaseClient() {
        return DatabaseClient.create(routingConnectionFactory);
    }


}
