package com.sysco.rps.repository.common;

import com.sysco.rps.entity.masterdata.BusinessUnit;
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
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.sysco.rps.common.Constants.JdbcProperties.JDBC_MYSQL;
import static com.sysco.rps.common.Constants.JdbcProperties.PORT;
import static com.sysco.rps.common.Constants.JdbcProperties.PRICINGDB;
import static com.sysco.rps.common.Constants.PRICINGDB_MAXAGE_LOWER_LIMIT_DEFAULT;
import static com.sysco.rps.common.Constants.PRICINGDB_MAXAGE_UPPER_LIMIT_DEFAULT;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.INITIAL_SIZE;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_SIZE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PROTOCOL;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

/**
 * This works as a scheduler to load connection pools for available OpCos
 * <p>
 * * @tag Copyright (C) 2018 SYSCO Corp. All Rights Reserved.
 */
@Component("activeDatabaseProvider")
@DependsOn({"businessUnitLoaderService"})
public class DataSourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceProvider.class);
    private final BusinessUnitLoaderService businessUnitLoaderService;
    private static final Map<String, ConnectionPool> dataSourceMap = new HashMap<>();
    private static final Set<String> businessUnitIds = new HashSet<>();

    @Value("${pricing.db.max.life.lower.limit}")
    private String strPricingDbMaxLifeLowerLimit;

    @Value("${pricing.db.max.life.upper.limit}")
    private String strPricingDbMaxLifeUpperLimit;

    @Value("${pricing.db.jdbcHost}")
    private String jdbcHost;

    @Value("${pricing.db.username}")
    private String jdbcUser;

    @Value("${pricing.db.password}")
    private String jdbcPassword;

    @Value(("${pricing.db.max.pool.size}"))
    private String maxPoolSize;

    @Value(("${pricing.db.initial.pool.size}"))
    private String initialPoolSize;

    @Value(("${pricing.db.max.idle.time}"))
    private String maxIdleTime;

    private long pricingDbMaxLifeLowerLimit;
    private long pricingDbMaxLifeUpperLimit;

    @Autowired
    public DataSourceProvider(BusinessUnitLoaderService loadBusinessUnitAction) {
        this.businessUnitLoaderService = loadBusinessUnitAction;
        this.pricingDbMaxLifeLowerLimit = PRICINGDB_MAXAGE_LOWER_LIMIT_DEFAULT;
        this.pricingDbMaxLifeUpperLimit = PRICINGDB_MAXAGE_UPPER_LIMIT_DEFAULT;
    }

    public static ConnectionPool getDataSource(String dbName) {
        return dataSourceMap.get(dbName);
    }

    /**
     * Scheduler task which configure the active database for the price sources
     */
    @Bean("loadDb")
//    @Scheduled(fixedRate = FIXED_RATE, initialDelay = 0)
    public void loadActiveDbs() {
        try {
            setMaxLifeTimeLimits();
            loadActiveBusinessUnits();
            updateActivePricingDbs();
        } catch (Exception e) {
            LOGGER.error("Error in refreshing active db properties for Pricing DBs", e);
        }
    }

    private void setMaxLifeTimeLimits() {

        if (strPricingDbMaxLifeLowerLimit != null) {
            this.pricingDbMaxLifeLowerLimit = Long.parseLong(strPricingDbMaxLifeLowerLimit);
        }

        if (strPricingDbMaxLifeUpperLimit != null) {
            this.pricingDbMaxLifeUpperLimit = Long.parseLong(strPricingDbMaxLifeUpperLimit);
        }
    }

    private void loadActiveBusinessUnits() {
        try {
            List<BusinessUnit> businessUnits = businessUnitLoaderService.loadBusinessUnitList();
            Set<String> activeBusinessUnitIds = businessUnits
                  .stream()
                  .map(BusinessUnit::getBusinessUnitNumber)
                  .collect(Collectors.toSet());
            businessUnitIds.clear();
            businessUnitIds.addAll(activeBusinessUnitIds);
        } catch (Exception e) {
            LOGGER.error("Error in loading active business units", e);
        }
    }

    /**
     * Update Pricing DBs for all the active bunits.
     */
    private void updateActivePricingDbs() {

        try {

            List<Object> datasourcesToBeClosed = null;

            for (String businessUnitId : businessUnitIds) {

                String db = PRICINGDB + businessUnitId;
                String activeDbUrlForCurrentBunit = JDBC_MYSQL + jdbcHost + PORT + "/" + db;

                if (dataSourceMap.get(businessUnitId) == null) {

                    datasourcesToBeClosed = datasourcesToBeClosed == null ? new ArrayList<>() : datasourcesToBeClosed;

                    LOGGER.info("Configuration started for {}", activeDbUrlForCurrentBunit);

                    try {
                        ConnectionFactory connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                              .option(DRIVER, "pool")
                              .option(PROTOCOL, "mysql")
                              .option(HOST, jdbcHost)
                              .option(USER, jdbcUser)
                              .option(PASSWORD, jdbcPassword)
                              .option(DATABASE, db)
                              .option(MAX_SIZE, getInt(maxPoolSize, 10))
                              .option(INITIAL_SIZE, getInt(initialPoolSize, 1))
                              .build());

                        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(connectionFactory)
                              .maxIdleTime(Duration.ofMillis(getInt(maxIdleTime, 1000)))
                              .maxLifeTime(Duration.ofMillis(getMaxLifeTimeRandomlyBasedOnLimits()))
                              .build();

                        ConnectionPool pool = new ConnectionPool(configuration);
                        Mono<Integer> warmup = pool.warmup();
                        warmup.subscribe();

                        warmup.map(val -> {
                            LOGGER.info("Warmed up {}", val);
                            return val;
                        });

                        dataSourceMap.put(businessUnitId, pool);
                    } catch (Exception e) {
                        LOGGER.error("Failed to form DB pool for database {}", businessUnitId);
                    }

                    LOGGER.info("Configuration completed for {}", activeDbUrlForCurrentBunit);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error in updating data Sources", e);
        }
    }

    // sets a random value for the maximum lifetime of a connection in the pool
    private long getMaxLifeTimeRandomlyBasedOnLimits() {
        return ThreadLocalRandom.current().nextLong(this.pricingDbMaxLifeLowerLimit, this.pricingDbMaxLifeUpperLimit + 1);
    }

    private int getInt(String strVal, int defaultVal) {
        return StringUtils.isEmpty(strVal) ? defaultVal : Integer.parseInt(strVal);
    }
}

