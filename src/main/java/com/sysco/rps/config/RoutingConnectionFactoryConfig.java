package com.sysco.rps.config;

import com.sysco.rps.entity.masterdata.BusinessUnit;
import com.sysco.rps.repository.common.RoutingConnectionFactory;
import com.sysco.rps.service.loader.BusinessUnitLoaderService;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.sysco.rps.common.Constants.JdbcProperties.PRICINGDB;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.INITIAL_SIZE;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_ACQUIRE_TIME;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_CREATE_CONNECTION_TIME;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_IDLE_TIME;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_LIFE_TIME;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_SIZE;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.VALIDATION_QUERY;
import static io.r2dbc.spi.ConnectionFactoryOptions.CONNECT_TIMEOUT;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PROTOCOL;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

/**
 * Configuration for Routing connection factory.
 * Uses the available business units and create a db config pool for each.
 * maxIdle and maxLifeTime values are randomized to better use Aurora load balancing.
 *
 * @author Tharuka Jayalath
 * (C) 2020, Sysco Corporation
 * Created: 7/6/20. Mon 2020 09:50
 */
@Configuration
public class RoutingConnectionFactoryConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingConnectionFactoryConfig.class);
    @Value("${pricing.db.max.life.lower.limit}")
    private Long pricingDbMaxLifeLowerLimit;
    @Value("${pricing.db.max.life.upper.limit}")
    private Long pricingDbMaxLifeUpperLimit;
    private BusinessUnitLoaderService businessUnitLoaderService;

    /***
     * Allows setting a business loader service
     * @param businessUnitLoaderService
     */
    @Autowired
    public RoutingConnectionFactoryConfig(BusinessUnitLoaderService businessUnitLoaderService) {
        this.businessUnitLoaderService = businessUnitLoaderService;
    }

    /***
     * Method to create RoutingConnectionFactory bean.
     * Will go through all the available business units and create a connection factory for each.
     * businessUnitId is used as the key when adding factories to the RoutingConnectionFactory.
     * See RoutingConnectionFactory's determineCurrentLookupKey implementation where it is defined how to determine this business unit ID as the
     * lookup key
     * @param jdbcHost
     * @param jdbcUser
     * @param jdbcPassword
     * @param maxPoolSize
     * @param initialPoolSize
     * @param validationQuery
     * @return RoutingConnectionFactory
     */
    @Bean
    public RoutingConnectionFactory routingConnectionFactory(@Value("${pricing.db.jdbcHost}") String jdbcHost,
                                                             @Value("${pricing.db.username}") String jdbcUser,
                                                             @Value("${pricing.db.password}") String jdbcPassword,
                                                             @Value("${pricing.db.max.pool.size}") String maxPoolSize,
                                                             @Value("${pricing.db.initial.pool.size}") String initialPoolSize,
                                                             @Value("${pricing.db.connection.validation.query}") String validationQuery
    ) {
        RoutingConnectionFactory routingConnectionFactory = new RoutingConnectionFactory();

        ConnectionFactory defaultConnectionFactory = null;

        Map<String, ConnectionFactory> factories = new HashMap<>();

        Set<String> activeBusinessUnitIds = loadActiveBusinessUnits();

        for (String businessUnitId : activeBusinessUnitIds) {

            String db = PRICINGDB + businessUnitId;

            Duration maxLife = Duration.ofMillis(getMaxLifeTimeRandomlyBasedOnLimits());
            Duration maxIdle = Duration.ofMillis(getMaxLifeTimeRandomlyBasedOnLimits());
            Duration connectionTimeout = Duration.ofSeconds(6000);
            Duration maxConnectionCreateTime = Duration.ofMillis(5000);
            Duration maxConnectionAcquireTime = Duration.ofMillis(10000);

            LOGGER.debug("Setting max times for conn pool [{}] Max Lifetime: [{} S], Max Idle Time [{} S]", db, maxLife.toSeconds(),
                  maxIdle.toSeconds());

            ConnectionFactory connectionFactory = ConnectionFactories.get(
                  ConnectionFactoryOptions.builder()
                        .option(DRIVER, "pool")
                        .option(PROTOCOL, "mysql")
                        .option(HOST, jdbcHost)
                        .option(USER, jdbcUser)
                        .option(PASSWORD, jdbcPassword)
                        .option(DATABASE, db)
                        .option(MAX_SIZE, getInt(maxPoolSize, 10))
                        .option(INITIAL_SIZE, getInt(initialPoolSize, 5))
                        .option(MAX_LIFE_TIME, maxLife)
                        .option(MAX_IDLE_TIME, maxIdle)
                        .option(CONNECT_TIMEOUT, connectionTimeout)
                        .option(MAX_ACQUIRE_TIME, maxConnectionAcquireTime)
                        .option(MAX_CREATE_CONNECTION_TIME, maxConnectionCreateTime)
                        .option(VALIDATION_QUERY, validationQuery)
                        .build()
            );

/*            ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(connectionFactory)
                  .maxSize(getInt(maxPoolSize, 10))
                  .initialSize(getInt(initialPoolSize, 5))
                  .maxLifeTime(maxLife)
                  .maxIdleTime(maxIdle)
                  .maxAcquireTime(maxConnectionAcquireTime)
                  .maxCreateConnectionTime(maxConnectionCreateTime)
                  .validationQuery(validationQuery)
                  .build();

            ConnectionPool pool = new ConnectionPool(configuration);*/

            if (defaultConnectionFactory == null) {
                defaultConnectionFactory = connectionFactory;
            }
            factories.put(businessUnitId, connectionFactory);
/*            pool.warmup()
                  .subscribe(connectionCount -> LOGGER.info("Created [{}] db connections for businessUnitId [{}]", connectionCount, businessUnitId));*/
        }

        routingConnectionFactory.setTargetConnectionFactories(factories);
        routingConnectionFactory.setDefaultTargetConnectionFactory(defaultConnectionFactory);
        return routingConnectionFactory;
    }

    private Set<String> loadActiveBusinessUnits() {
        return this.businessUnitLoaderService.loadBusinessUnitList()
              .stream()
              .map(BusinessUnit::getBusinessUnitNumber)
              .collect(Collectors.toSet());
    }

    private long getMaxLifeTimeRandomlyBasedOnLimits() {
        return ThreadLocalRandom.current().nextLong(pricingDbMaxLifeLowerLimit, pricingDbMaxLifeUpperLimit + 1);
    }

    private int getInt(String strVal, int defaultVal) {
        return StringUtils.isEmpty(strVal) ? defaultVal : Integer.parseInt(strVal);
    }
}
