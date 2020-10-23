package com.sysco.rps.controller;

import com.sysco.rps.BaseTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InfoControllerTest extends BaseTest {

    InfoController infoController;

    @Autowired
    public InfoControllerTest(InfoController infoController) {
        this.infoController = infoController;
    }

    /***
     * Testing PRCP-2487
     */
    @Test
    @Tag("desc:PRCP-2487")
    void getConnectionPoolInfo() {
        StepVerifier.create(infoController.getConnectionPoolInfo())
                .consumeNextWith(result -> {
                    assertNotNull(result);
                    assertEquals(0, result.size());
                })
                .verifyComplete();
    }

}
