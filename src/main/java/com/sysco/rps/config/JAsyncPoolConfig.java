package com.sysco.rps.config;

import com.github.jasync.sql.db.ConnectionPoolConfiguration;
import com.github.jasync.sql.db.mysql.MySQLConnection;
import com.github.jasync.sql.db.mysql.pool.MySQLConnectionFactory;
import com.github.jasync.sql.db.pool.ConnectionPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 10. Jun 2020 12:00
 */

@Configuration
public class JAsyncPoolConfig {

    private static String host = "reference-db-mysql-cluster.cluster-ro-c6xai0tt38eb.us-east-1.rds.amazonaws.com";
    private static int port = 3306;
    private static String db = "reference_pricing";
    private static String username = "admin";
    private static String password = "gvt12345";
    private static int maxActiveConnections = 200;


    @Bean(name = "jAsyncDataSource")
    public ConnectionPool<MySQLConnection> getDataSource() {

        // Not particularly clear why we need to provide host etc in pool config as well
        ConnectionPoolConfiguration poolConfiguration = new ConnectionPoolConfiguration(
              host,
              port,
              db,
              username,
              password,
              maxActiveConnections,
              TimeUnit.MINUTES.toMillis(15)
        );

        MySQLConnectionFactory mySQLConnectionFactory = new MySQLConnectionFactory(new com.github.jasync.sql.db.Configuration(
              username,
              host,
              port,
              password,
              db
        ));


        return new ConnectionPool<>(mySQLConnectionFactory, poolConfiguration);
    }

}
