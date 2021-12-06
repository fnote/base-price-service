package com.sysco.rps.repository.refpricing;

import com.sysco.rps.common.Constants;
import com.sysco.rps.config.PriceZoneMasterDataRecord;
import com.sysco.rps.entity.masterdata.BusinessUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.LocalDateTime;
import java.util.Map;

import static com.sysco.rps.common.Constants.ROUTING_KEY;

/**
 * Repository that provides access to the Master Data of Reference Price components
 * @author Tharuka Jayalath
 * @copyright (C) 2021, Sysco Corporation
 * @end Created : 09/14/2021. Tue 12.42
 */
@Repository
public class MasterDataRepository {

    private static final String MASTER_DATA_FETCHING_QUERY = "SELECT * FROM " + Constants.DBNames.PRICE_ZONE_MASTER_DATA;

    private final DatabaseClient databaseClient;

    @Autowired
    MasterDataRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    /**
     * Provides Master data of PRICE_ZONE data storage tables
     * @param businessUnit BusinessUnit
     * @return Mono<Map<String, PriceZoneMasterDataRecord>>
     */
    public Mono<Map<String, PriceZoneMasterDataRecord>> getPZMasterDataByOpCo(BusinessUnit businessUnit) {
        return databaseClient
                .execute(MASTER_DATA_FETCHING_QUERY)
                .map(row -> new PriceZoneMasterDataRecord(
                        row.get(Constants.DBNames.COLUMN_TABLE_TYPE, String.class),
                        row.get(Constants.DBNames.COLUMN_TABLE_NAME, String.class),
                        row.get(Constants.DBNames.COLUMN_EFFECTIVE_DATE, LocalDateTime.class)
                ))
                .all()
                .subscriberContext(Context.of(ROUTING_KEY, businessUnit.getBusinessUnitNumber()))
                .collectMap(PriceZoneMasterDataRecord::getTableType);
    }

}
