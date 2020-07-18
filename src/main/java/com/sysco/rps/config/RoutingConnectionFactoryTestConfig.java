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

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.sysco.rps.common.Constants.JdbcProperties.PRICINGDB;

/**
 * Provides DB connection configurations to connect with an in-memory H2 DB
 *
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

    @Autowired
    public RoutingConnectionFactoryTestConfig(BusinessUnitLoaderService businessUnitLoaderService) {
        this.businessUnitLoaderService = businessUnitLoaderService;
    }

    @Bean
    public RoutingConnectionFactory routingConnectionFactory(@Value("${pricing.db.username}") String jdbcHost,
                                                             @Value("${pricing.db.username}") String jdbcUser,
                                                             @Value("${pricing.db.password}") String jdbcPassword,
                                                             @Value("${pricing.db.h2.file.path}") String dbFilePath,
                                                             @Value("${pricing.db.max.life.lower.limit}") Long pricingDbMaxLifeLowerLimit,
                                                             @Value("${pricing.db.max.life.upper.limit}") Long pricingDbMaxLifeUpperLimit) {
        RoutingConnectionFactory router = new RoutingConnectionFactory();

        ConnectionFactory defaultConnectionFactory = null;

        Map<String, ConnectionFactory> factories = new HashMap<>();

        Set<String> activeBusinessUnitIds = loadActiveBusinessUnits();

        for (String businessUnitId : activeBusinessUnitIds) {

            String db = PRICINGDB + businessUnitId;

            Duration maxLife = Duration.ofMillis(getMaxLifeTimeRandomlyBasedOnLimits(pricingDbMaxLifeLowerLimit, pricingDbMaxLifeUpperLimit));
            Duration maxIdle = Duration.ofMillis(getMaxLifeTimeRandomlyBasedOnLimits(pricingDbMaxLifeLowerLimit, pricingDbMaxLifeUpperLimit));

            LOGGER.debug("Setting max times for conn pool [{}] Max Lifetime: [{} S], Max Idle Time [{} S]", db, maxLife.toSeconds(),
                  maxIdle.toSeconds());

            //@TODO sanjayaa: remove commented lines
            ConnectionFactory connectionFactory = new H2ConnectionFactory(
                  H2ConnectionConfiguration.builder()
                        .username(jdbcUser)
                        .password(jdbcPassword)
//                  .tcp("localhost", "/Users/sanjayaa/Desktop/pricing/db/test")
//                  .inMemory("REF_PRICE_020")
                        .file(dbFilePath)
                        .property(H2ConnectionOption.MODE, MYSQL)
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

    private long getMaxLifeTimeRandomlyBasedOnLimits(Long pricingDbMaxLifeLowerLimit, Long pricingDbMaxLifeUpperLimit) {
        return ThreadLocalRandom.current().nextLong(pricingDbMaxLifeLowerLimit, pricingDbMaxLifeUpperLimit + 1);
    }

}
