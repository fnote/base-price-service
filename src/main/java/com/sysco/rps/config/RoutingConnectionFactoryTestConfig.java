package com.sysco.rps.config;

import com.sysco.rps.entity.masterdata.BusinessUnit;
import com.sysco.rps.repository.common.RoutingConnectionFactory;
import com.sysco.rps.service.loader.BusinessUnitLoaderService;
import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.h2.H2ConnectionOption;
import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides DB connection configurations to connect with an in-memory H2 DB
 * Used for unit testing
 * Note: Though this serves as configurations for a RoutingConnectionFactory, currently only a single DB can be used due to a limitation in the
 * data loader
 * @author Sanjaya Amarasinghe
 * (C) 2020, Sysco Corporation
 * Created: 7/17/20. Fri 2020 11:20
 */
@Profile("test")
@Configuration
public class RoutingConnectionFactoryTestConfig {

    private static final String MYSQL = "MySQL";
    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingConnectionFactoryTestConfig.class);
    private BusinessUnitLoaderService businessUnitLoaderService;

    /***
     * Allows setting a business loader service
     * @param businessUnitLoaderService
     */
    @Autowired
    public RoutingConnectionFactoryTestConfig(BusinessUnitLoaderService businessUnitLoaderService) {
        this.businessUnitLoaderService = businessUnitLoaderService;
    }

    /***
     * Method to create RoutingConnectionFactory bean.
     * @param jdbcUser
     * @param jdbcPassword
     * @param dbFilePath
     * @return RoutingConnectionFactory
     */
    @Bean
    public RoutingConnectionFactory routingConnectionFactory(
          @Value("${pricing.db.username}") String jdbcUser,
          @Value("${pricing.db.password}") String jdbcPassword,
          @Value("${pricing.db.h2.file.path}") String dbFilePath) {
        RoutingConnectionFactory router = new RoutingConnectionFactory();

        ConnectionFactory defaultConnectionFactory = null;

        Map<String, ConnectionFactory> factories = new HashMap<>();

        Set<String> activeBusinessUnitIds = loadActiveBusinessUnits();

        // TODO: consider using business unit ID for DB File path

        for (String businessUnitId : activeBusinessUnitIds) {

            // Note: Can use H2 db in memory by using .inMemory("REF_PRICE_020") where REF_PRICE_020 refers to the DB name
            // However, an in memory instance is private for the connection. Hence using the file based DB in the connection config below
            // If required DB can be started in TCP mode using .tcp("localhost", "~/tmp/test")
            ConnectionFactory connectionFactory = new H2ConnectionFactory(
                  H2ConnectionConfiguration.builder()
                        .username(jdbcUser)
                        .password(jdbcPassword)
                        .file(dbFilePath)
                        .property(H2ConnectionOption.MODE, MYSQL)
                        .build()
            );

            if (defaultConnectionFactory == null) {
                defaultConnectionFactory = connectionFactory;
            }

            factories.put(businessUnitId, connectionFactory);

            LOGGER.info("Created connection factory for DB [{}]", businessUnitId);
        }

        router.setTargetConnectionFactories(factories);
        router.setDefaultTargetConnectionFactory(defaultConnectionFactory);
        return router;
    }

    private Set<String> loadActiveBusinessUnits() {
        return this.businessUnitLoaderService.loadBusinessUnitList()
              .stream()
              .map(BusinessUnit::getBusinessUnitNumber)
              .collect(Collectors.toSet());
    }

}
