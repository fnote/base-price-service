package com.sysco.rps.config;

import com.sysco.rps.common.Constants;
import com.sysco.rps.repository.common.RoutingConnectionFactory;
import com.sysco.rps.service.loader.BusinessUnitLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.util.context.Context;

import java.time.LocalDateTime;
import java.util.Map;

import static com.sysco.rps.common.Constants.ROUTING_KEY;

/**
 * Configuration class for initializing Map<String, PriceZoneTableConfig> bean for test profile.
 *
 * @author Tharuka Jayalath
 * (C) 2021, Sysco Corporation
 * Created: 3/18/22. Thu 14:00
 */
@Profile("test")
@Configuration
public class PriceZoneTableTestConfigInitializer {

    private static final Logger logger = LoggerFactory.getLogger(PriceZoneTableTestConfigInitializer.class);

    private static final String MASTER_DATA_FETCHING_QUERY = "SELECT * FROM " + Constants.DBNames.PRICE_ZONE_MASTER_DATA;

    private final DatabaseClient databaseClient;

    private final RoutingConnectionFactory routingConnectionFactory;

    private final BusinessUnitLoaderService businessUnitLoaderService;

    @Autowired
    public PriceZoneTableTestConfigInitializer(DatabaseClient databaseClient,
                                               RoutingConnectionFactory routingConnectionFactory,
                                               BusinessUnitLoaderService businessUnitLoaderService) {
        this.databaseClient = databaseClient;
        this.routingConnectionFactory = routingConnectionFactory;
        this.businessUnitLoaderService = businessUnitLoaderService;
    }


    @Bean
    public Map<String, PriceZoneTableConfig> initPriceZoneTableConfig() {

        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource[] scripts = new Resource[]{resourceLoader.getResource("classpath:schema.sql")};

        new ResourceDatabasePopulator(scripts).execute(routingConnectionFactory)
                .doOnError(e -> logger.error("Failed to load initial data", e))
                .doOnSuccess(s -> logger.info("Successfully loaded initial data"))
                .block();

        return Flux.fromIterable(businessUnitLoaderService.loadBusinessUnitList())
                .map(businessUnit -> {
                    Map<String, PriceZoneMasterDataRecord> masterDataRecordMap = databaseClient.execute(MASTER_DATA_FETCHING_QUERY)
                            .map(row -> new PriceZoneMasterDataRecord(row.get(Constants.DBNames.COLUMN_TABLE_TYPE, String.class),
                                    row.get(Constants.DBNames.COLUMN_TABLE_NAME, String.class),
                                    row.get(Constants.DBNames.COLUMN_EFFECTIVE_DATE, LocalDateTime.class)
                            ))
                            .all()
                            .subscriberContext(Context.of(ROUTING_KEY, businessUnit.getBusinessUnitNumber()))
                            .collectMap(PriceZoneMasterDataRecord::getTableType)
                            .block();

                    assert masterDataRecordMap != null;
                    PriceZoneMasterDataRecord active = masterDataRecordMap.get(Constants.DBNames.PRICE_ZONE_TABLE_TYPE_ACTIVE);
                    PriceZoneMasterDataRecord history = masterDataRecordMap.get(Constants.DBNames.PRICE_ZONE_TABLE_TYPE_HISTORY);

                    return new PriceZoneTableConfig(businessUnit.getBusinessUnitNumber(), active.getTableName(),
                            history.getTableName(), active.getEffectiveDate());

                })
                .collectMap(PriceZoneTableConfig::getBusinessUnitNumber)
                .block();

    }

}
