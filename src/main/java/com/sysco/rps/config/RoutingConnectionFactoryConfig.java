package com.sysco.rps.config;

import com.sysco.rps.entity.masterdata.BusinessUnit;
import com.sysco.rps.repository.common.RoutingConnectionFactory;
import com.sysco.rps.service.loader.BusinessUnitLoaderService;
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

import static io.r2dbc.pool.PoolingConnectionFactoryProvider.INITIAL_SIZE;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_IDLE_TIME;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_LIFE_TIME;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_SIZE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PROTOCOL;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;
import static com.sysco.rps.common.Constants.JdbcProperties.PRICINGDB;


/**
 * @author Tharuka Jayalath
 * (C) 2019, Sysco Labs
 * Created: 7/6/20. Mon 2020 09:50
 */
@Configuration
public class RoutingConnectionFactoryConfig {

    @Value("${pricing.db.jdbcHost}")
    private String jdbcHost;

    @Value("${pricing.db.username}")
    private String jdbcUser;

    @Value("${pricing.db.password}")
    private String jdbcPassword;

    @Value("${pricing.db.max.pool.size}")
    private String maxPoolSize;

    @Value("${pricing.db.initial.pool.size}")
    private String initialPoolSize;

    @Value("${pricing.db.max.idle.time}")
    private String maxIdleTime;

    @Value("${pricing.db.max.life.time}")
    private String maxLifeTime;

    @Value("${pricing.db.max.life.lower.limit}")
    private Long pricingDbMaxLifeLowerLimit;

    @Value("${pricing.db.max.life.upper.limit}")
    private Long pricingDbMaxLifeUpperLimit;

    private BusinessUnitLoaderService businessUnitLoaderService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingConnectionFactoryConfig.class);

    @Autowired
    public RoutingConnectionFactoryConfig(BusinessUnitLoaderService businessUnitLoaderService) {
        this.businessUnitLoaderService = businessUnitLoaderService;
    }


    @Bean
    public RoutingConnectionFactory routingConnectionFactory() {
        RoutingConnectionFactory router =  new RoutingConnectionFactory();

        ConnectionFactory defaultConnectionFactory = null;

        Map<String, ConnectionFactory> factories = new HashMap<>();

        Set<String> activeBusinessUnitIds = loadActiveBusinessUnits();

        for (String businessUnitId: activeBusinessUnitIds) {

            String db = PRICINGDB + businessUnitId;

            Duration maxLife = Duration.ofMillis(getMaxLifeTimeRandomlyBasedOnLimits());
            Duration maxIdle = Duration.ofMillis(getMaxLifeTimeRandomlyBasedOnLimits());

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
                        .option(INITIAL_SIZE, getInt(maxPoolSize, 1))
                        .option(MAX_IDLE_TIME, maxIdle)
                        .option(MAX_LIFE_TIME, maxLife)
                        .build()
            );

            if (defaultConnectionFactory == null) {
                defaultConnectionFactory = connectionFactory;
            }
            factories.put(businessUnitId, connectionFactory);
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

    private long getMaxLifeTimeRandomlyBasedOnLimits() {
        return ThreadLocalRandom.current().nextLong(pricingDbMaxLifeLowerLimit, pricingDbMaxLifeUpperLimit + 1);
    }

    private int getInt(String strVal, int defaultVal) {
        return StringUtils.isEmpty(strVal) ? defaultVal : Integer.parseInt(strVal);
    }

}
