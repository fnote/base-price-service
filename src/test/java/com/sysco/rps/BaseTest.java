package com.sysco.rps;

import com.sysco.rps.repository.common.RoutingConnectionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 13. Jul 2020 18:21
 */
//@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

//    @Autowired
//    RoutingConnectionFactory routingConnectionFactory;
//
//    @BeforeAll
//    void updateDBs() {
//        ResourceLoader resourceLoader = new DefaultResourceLoader();
//        Resource[] scripts = new Resource[]{resourceLoader.getResource("classpath:schema.sql"),
//              resourceLoader.getResource("classpath:data.sql")};
//
//        new ResourceDatabasePopulator(scripts).execute(routingConnectionFactory).block();
//    }
}
