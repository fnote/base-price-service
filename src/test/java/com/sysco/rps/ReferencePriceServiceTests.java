package com.sysco.rps;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ReferencePriceServiceTests {

    private String activeProfile;

    ReferencePriceServiceTests(@Value("${spring.profiles.active}") String activeProfile) {
        this.activeProfile = activeProfile;
    }

    @Test
    void contextLoads() {
        assertNotNull(activeProfile);
        assertNotEquals("prod", activeProfile);
    }

}
