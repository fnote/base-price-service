package com.sysco.rps.config;

import com.sysco.rps.service.MasterDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.sysco.rps.common.Constants.SCHEDULER_REFRESH_INTERVAL;

@Component
public class PriceZoneTableConfigReloader {

    private static Logger logger = LoggerFactory.getLogger(PriceZoneTableConfigReloader.class);

    @Autowired
    private MasterDataService masterDataService;

    @Scheduled(fixedRate = SCHEDULER_REFRESH_INTERVAL)
    public void reloadPriceZoneTableConfig() {
        masterDataService.refreshPriceZoneMasterData().block();
        logger.info("PriceZoneMasterData has been reloaded.");
    }
}