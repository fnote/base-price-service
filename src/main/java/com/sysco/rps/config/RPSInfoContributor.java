package com.sysco.rps.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/***
 * This class contributes to add custom information to the /status endpoint
 */
@Component
public class RPSInfoContributor implements InfoContributor {

    @Value("${active.business.units}")
    private String activeBusinessUnits;

    private Environment env;

    @Autowired
    private Map<String, PriceZoneTableConfig> initPriceZoneTableConfig;

    public RPSInfoContributor(Environment env) {
        this.env = env;
    }

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, String> infoAttributes = new HashMap<>();
        infoAttributes.put("activeBusinessUnits", activeBusinessUnits);
        infoAttributes.put("activeProfile", Arrays.toString(env.getActiveProfiles()));

        builder.withDetail("configs", infoAttributes);
        builder.withDetail("priceZoneMasterData", initPriceZoneTableConfig.values());
    }
}
