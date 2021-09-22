package com.sysco.rps.service;

import com.sysco.rps.config.PriceZoneTableConfig;
import com.sysco.rps.repository.refpricing.MasterDataRepository;
import com.sysco.rps.service.loader.BusinessUnitLoaderService;
import com.sysco.rps.util.MasterDataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Tharuka Jayalath
 * @copyright (C) 2021, Sysco Corporation
 * @end Created : 09/14/2021. Tue 13.16
 */
@Service
public class MasterDataService {

    private final MasterDataRepository masterDataRepository;

    private final BusinessUnitLoaderService businessUnitLoaderService;

    private final Map<String, PriceZoneTableConfig> priceZoneTableConfigMap;

    @Autowired
    MasterDataService(MasterDataRepository masterDataRepository,
                      BusinessUnitLoaderService businessUnitLoaderService,
                      Map<String, PriceZoneTableConfig> priceZoneTableConfigMap) {
        this.masterDataRepository = masterDataRepository;
        this.businessUnitLoaderService = businessUnitLoaderService;
        this.priceZoneTableConfigMap = priceZoneTableConfigMap;
    }

    public Mono<Void> refreshPriceZoneMasterData() {
        return Flux
                .fromIterable(businessUnitLoaderService.loadBusinessUnitList())
                .flatMap(businessUnit -> masterDataRepository
                        .getPZMasterDataByOpCo(businessUnit)
                        .flatMap(masterDataRecordMap -> {
                            PriceZoneTableConfig config = MasterDataUtils.constructPriceZoneTableConfig(businessUnit, masterDataRecordMap);
                            priceZoneTableConfigMap.replace(businessUnit.getBusinessUnitNumber(), config);
                            return Mono.empty();
                        })
                )
                .collectList()
                .flatMap(k -> Mono.empty());
    }
}
