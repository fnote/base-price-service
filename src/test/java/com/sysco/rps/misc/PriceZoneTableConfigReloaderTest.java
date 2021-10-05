package com.sysco.rps.misc;

import com.sysco.rps.config.PriceZoneTableConfigReloader;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PriceZoneTableConfigReloaderTest {

    @SpyBean
    private PriceZoneTableConfigReloader priceZoneTableConfigReloader;

    @Test
    public void testRefreshSchedulingForMasterDataService() {
        await()
              .atMost(Duration.ONE_SECOND)
              .untilAsserted(() -> verify(priceZoneTableConfigReloader, times(1)).reloadPriceZoneTableConfig());
    }

}