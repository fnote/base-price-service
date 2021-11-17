package com.sysco.rps.config;

import com.sysco.rps.service.MasterDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PriceZoneTableConfigReloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceZoneTableConfigReloader.class);

    private final MasterDataService masterDataService;

    @Autowired
    public PriceZoneTableConfigReloader(MasterDataService masterDataService) {
        this.masterDataService = masterDataService;
    }

    @Scheduled(fixedRate = 1800000)
    public void reloadPriceZoneTableConfig() {
        masterDataService.refreshPriceZoneMasterData().block();
        LOGGER.info("PriceZoneMasterData has been reloaded.");
    }

}
