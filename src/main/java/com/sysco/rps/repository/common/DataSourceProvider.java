package com.sysco.rps.repository.common;


import com.sysco.rps.entity.pp.masterdata.BusinessUnit;
import com.sysco.rps.service.loader.BusinessUnitLoaderService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.sysco.rps.common.Constants.JdbcProperties.HIKARI_POOL_NAME_SUFFIX;
import static com.sysco.rps.common.Constants.JdbcProperties.JDBC_DRIVER_NAME;
import static com.sysco.rps.common.Constants.JdbcProperties.JDBC_MYSQL;
import static com.sysco.rps.common.Constants.JdbcProperties.PORT;
import static com.sysco.rps.common.Constants.JdbcProperties.PRICINGDB;

/**
 * This works as a scheduler to load connection pools for available OpCos
 *
 * * @tag Copyright (C) 2018 SYSCO Corp. All Rights Reserved.
 */
@Component("activeDatabaseProvider")
@DependsOn({"businessUnitLoaderService"})
public class DataSourceProvider {

    private static final RoutingDataSource updatedRoutingDataSource = new RoutingDataSource();
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceProvider.class);
    private static Map<Object, Object> targetDataSources = new HashMap<>();
    private final BusinessUnitLoaderService businessUnitLoaderService;
    private final String envPrefix;
    private List<BusinessUnit> businessUnits = null;

    @Value("${pricing.db.max.life.lower.limit}")
    private long pricingDbMaxLifeLowerLimit;

    @Value("${pricing.db.max.life.upper.limit}")
    private long pricingDbMaxLifeUpperLimit;

    @Value("${pricing.db.jdbcHost}")
    private String jdbcHost;

    @Value("${pricing.db.username}")
    private String jdbcUser;

    @Value("${pricing.db.password}")
    private String jdbcPassword;


    @Autowired
    public DataSourceProvider(BusinessUnitLoaderService loadBusinessUnitAction) {
        this.businessUnitLoaderService = loadBusinessUnitAction;
        this.envPrefix = "d";
    }

    public static RoutingDataSource getActiveDataSource() {
        return updatedRoutingDataSource;
    }

    /**
     * Scheduler task which configure the active database for the price sources
     */
    @Bean("loadDb")
//    @Scheduled(fixedRate = FIXED_RATE, initialDelay = 0)
    public void loadActiveDbs() {
        try {
            loadActiveBusinessUnits();
            updateActivePricingDbs();
        } catch (Exception e) {
            LOGGER.error("Error in refreshing active db properties for Pricing DBs", e);
        }
    }

    private void loadActiveBusinessUnits() {
        try {
            businessUnits = businessUnitLoaderService.loadBusinessUnitList();
            Set<String> activeBusinessUnitIds = businessUnits
                  .stream()
                  .map(BusinessUnit::getBusinessUnitNumber)
                  .collect(Collectors.toSet());
            DBConnectionContextHolder.setEnabledDatabases(activeBusinessUnitIds);
        } catch (Exception e) {
            LOGGER.error("Error in loading active business units", e);
        }
    }

    /**
     * Update Pricing DBs for all the active bunits.
     */
    private void updateActivePricingDbs() {
        boolean updated = false;

        Set<String> activeBusinessUnitIds = DBConnectionContextHolder.getEnabledDatabases();
        try {

            List<Object> datasourcesToBeClosed = null;

            for (String businessUnitId : activeBusinessUnitIds) {

                String activeDbUrlForCurrentBunit = JDBC_MYSQL + jdbcHost + PORT + PRICINGDB +
                      businessUnitId;
//                      + envPrefix;

                if (targetDataSources.get(businessUnitId) == null
                      || !activeDbUrlForCurrentBunit.equals(((HikariDataSource) targetDataSources.get(businessUnitId)).getJdbcUrl())) {

                    datasourcesToBeClosed = datasourcesToBeClosed == null ? new ArrayList<>() : datasourcesToBeClosed;

                    LOGGER.info("Configuration started for {}", activeDbUrlForCurrentBunit);

                    HikariConfig hikariConfig = new HikariConfig();
                    hikariConfig.setDriverClassName(JDBC_DRIVER_NAME);
                    hikariConfig.setJdbcUrl(activeDbUrlForCurrentBunit);
                    hikariConfig.setUsername(jdbcUser);
                    hikariConfig.setPassword(jdbcPassword);
                    hikariConfig.setPoolName(businessUnitId + HIKARI_POOL_NAME_SUFFIX);
//                    hikariConfig.setMaximumPoolSize(Integer.parseInt(SettingsProvider.getPropertyString(GROUP_NAME_CP_SYSTEM,
//                          Constants.SettingId.HIKARI_MAX_POOL_SIZE)));
//                    hikariConfig.setMinimumIdle(Integer.parseInt(SettingsProvider.getPropertyString(GROUP_NAME_CP_SYSTEM,
//                          Constants.SettingId.HIKARI_CONNECTION_MIN_IDLE_COUNT)));
//                    hikariConfig.setConnectionTimeout(Long.parseLong(SettingsProvider.getPropertyString(GROUP_NAME_CP_SYSTEM,
//                          Constants.SettingId.HIKARI_CONNECTION_TIMEOUT)));
                    hikariConfig.setMaxLifetime(getMaxLifeTimeRandomlyBasedOnLimits());
                    hikariConfig.addDataSourceProperty("cachePrepStmts", true);
                    hikariConfig.addDataSourceProperty("prepStmtCacheSize", 500);
                    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
                    hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
                    hikariConfig.setMaximumPoolSize(1);
                    hikariConfig.setMinimumIdle(1);
                    try {
                        datasourcesToBeClosed.add(targetDataSources.put(businessUnitId, new HikariDataSource(hikariConfig)));
                        updated = true;
                        LOGGER.info("Configuration completed for {}", activeDbUrlForCurrentBunit);

                    } catch (Exception e) {
                        LOGGER.error("Configuration failed for " + activeDbUrlForCurrentBunit, e);
                    }
                }
            }
            if (updated) {
                LOGGER.info("Updating the data sources");
                updatedRoutingDataSource.setTargetDataSources(targetDataSources);
                updatedRoutingDataSource.afterPropertiesSet();
                datasourcesToBeClosed.forEach(datasourceToBeClosed -> {
                    if (datasourceToBeClosed != null) {
                        ((HikariDataSource) datasourceToBeClosed).close();
                    }
                });
            } else {
                LOGGER.info("Data Sources not changed for all opcos");
            }

        } catch (Exception e) {
            LOGGER.error("Error in updating data Sources", e);
        }
    }

    private long getMaxLifeTimeRandomlyBasedOnLimits() {
        return ThreadLocalRandom.current().nextLong(this.pricingDbMaxLifeLowerLimit, this.pricingDbMaxLifeUpperLimit + 1);
    }
}

