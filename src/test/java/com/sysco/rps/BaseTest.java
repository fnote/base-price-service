package com.sysco.rps;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.springframework.MariaDB4jSpringService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 13. Jul 2020 18:21
 */
//@SpringBootTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-junit.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
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
//
//    }


    private static MariaDB4jSpringService DB;

    @BeforeClass
    public static void init() throws ManagedProcessException {
        DB = new MariaDB4jSpringService();
        DB.setDefaultPort(1234);
        DB.start();
        DB.getDB().createDB("yourtables");
        DB.getDB().source("schema.sql"); // init scripts from /src/test/resources/schema.sql
    }

    @AfterClass
    public static void cleanup() {
        if (DB != null) DB.stop();
    }


}
