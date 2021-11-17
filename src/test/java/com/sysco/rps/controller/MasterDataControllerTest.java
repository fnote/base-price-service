package com.sysco.rps.controller;

import com.sysco.rps.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

/**
 * @author Tharuka Jayalath
 * @copyright (C) 2021, Sysco Corporation
 * @end Created : 11/17/2021. Wed 13.02
 */
public class MasterDataControllerTest extends BaseTest {
    private final MasterDataController masterDataController;

    @Autowired
    MasterDataControllerTest(MasterDataController masterDataController) {
        this.masterDataController = masterDataController;
    }

    @Test
    void testRefreshPriceZoneMasterData() {
        StepVerifier.create(masterDataController.refreshPriceZoneMasterData())
                .verifyComplete();
    }
}
