package com.sysco.rps.controller;

import com.sysco.rps.BaseTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SupportControllerTest extends BaseTest {

    private SupportController supportController;

    @Autowired
    SupportControllerTest(SupportController infoController) {
        this.supportController = infoController;
    }

    /***
     * Testing PRCP-2487
     */
    @Test
    @Tag("desc:PRCP-2487")
    void getConnectionPoolInfo() {
        StepVerifier.create(supportController.getConnectionPoolInfo())
                .consumeNextWith(result -> {
                    assertNotNull(result);
                    assertEquals(1, result.size());
                })
                .verifyComplete();
    }

}
