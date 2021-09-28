package com.sysco.rps.config;

import com.sysco.rps.common.Constants;
import com.sysco.rps.service.loader.BusinessUnitLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.util.context.Context;

import java.time.LocalDateTime;
import java.util.Map;

import static com.sysco.rps.common.Constants.ROUTING_KEY;

/**
 * Configuration class for initializing Map<String, PriceZoneTableConfig> bean.
 *
 * @author Tharuka Jayalath
 * (C) 2021, Sysco Corporation
 * Created: 3/18/22. Thu 14:00
 */
@Profile("!test")
@Configuration
public class PriceZoneTableConfigInitializer {

    private static final String MASTER_DATA_FETCHING_QUERY = "SELECT * FROM " + Constants.DBNames.PRICE_ZONE_MASTER_DATA;

    private final DatabaseClient databaseClient;

    private final BusinessUnitLoaderService businessUnitLoaderService;

    @Autowired
    public PriceZoneTableConfigInitializer(DatabaseClient databaseClient,
                                           BusinessUnitLoaderService businessUnitLoaderService) {
        this.databaseClient = databaseClient;
        this.businessUnitLoaderService = businessUnitLoaderService;
    }

    @Bean
    public Map<String, PriceZoneTableConfig> initPriceZoneTableConfig() {
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
                    PriceZoneMasterDataRecord future = masterDataRecordMap.get(Constants.DBNames.PRICE_ZONE_TABLE_TYPE_FUTURE);
                    String historyTableName = history != null ? history.getTableName() : active.getTableName();
                    String futureTableName = future != null ? future.getTableName() : active.getTableName();
                    return new PriceZoneTableConfig(businessUnit.getBusinessUnitNumber(), active.getTableName(),
                            historyTableName, futureTableName, active.getEffectiveDate());

                })
                .collectMap(PriceZoneTableConfig::getBusinessUnitNumber)
                .block();

    }

}
