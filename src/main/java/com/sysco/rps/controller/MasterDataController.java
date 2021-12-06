package com.sysco.rps.controller;

import com.sysco.rps.service.MasterDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Controller for the reference price master data
 *
 * @author Tharuka Jayalath
 * @copyright (C) 2021, Sysco Corporation
 * @end Created : 09/14/2021. Tue 13.15
 */
@RestController
@RequestMapping("/master-data")
public class MasterDataController {

    private final MasterDataService masterDataService;

    @Autowired
    MasterDataController(MasterDataService masterDataService) {
        this.masterDataService = masterDataService;
    }

    /**
     * Exposes price-zone master data refreshing functionality
     *
     * @return Mono<Void>
     */
    @PostMapping("/price-zone/refresh")
    public Mono<Void> refreshPriceZoneMasterData() {
        return masterDataService.refreshPriceZoneMasterData();
    }
}
