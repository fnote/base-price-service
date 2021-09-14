package com.sysco.rps.config;

import com.sysco.rps.common.Constants;
import com.sysco.rps.repository.refpricing.MasterDataRepository;
import com.sysco.rps.service.loader.BusinessUnitLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Flux;

import java.util.Map;

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

    private final MasterDataRepository masterDataRepository;

    private final BusinessUnitLoaderService businessUnitLoaderService;

    @Autowired
    public PriceZoneTableConfigInitializer(MasterDataRepository masterDataRepository,
                                           BusinessUnitLoaderService businessUnitLoaderService) {
        this.masterDataRepository = masterDataRepository;
        this.businessUnitLoaderService = businessUnitLoaderService;
    }

    @Bean
    public Map<String, PriceZoneTableConfig> initPriceZoneTableConfig() {
        return Flux.fromIterable(businessUnitLoaderService.loadBusinessUnitList())
                .map(businessUnit -> {
                    Map<String, PriceZoneMasterDataRecord> masterDataRecordMap = masterDataRepository
                            .getPZMasterDataByOpCo(businessUnit)
                            .block();

                    assert masterDataRecordMap != null;
                    PriceZoneMasterDataRecord active = masterDataRecordMap.get(Constants.DBNames.PRICE_ZONE_TABLE_TYPE_ACTIVE);
                    PriceZoneMasterDataRecord history = masterDataRecordMap.get(Constants.DBNames.PRICE_ZONE_TABLE_TYPE_HISTORY);
                    String historyTableName = history != null ? history.getTableName() : active.getTableName();
                    return new PriceZoneTableConfig(businessUnit.getBusinessUnitNumber(), active.getTableName(),
                            historyTableName, active.getEffectiveDate());

                })
                .collectMap(PriceZoneTableConfig::getBusinessUnitNumber)
                .block();

    }

}
