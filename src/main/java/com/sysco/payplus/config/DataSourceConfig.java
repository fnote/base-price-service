package com.sysco.payplus.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/13/20 Time: 12:54 PM
 */
@Configuration
public class DataSourceConfig {


    @Value("${resolved.db.username}")
    private String dbUserName;
    @Value("${resolved.db.password}")
    private String dbPassword;
    @Value("${resolved.db.jdbcUrl}")
    private String jdbcUrl;


    @Bean
    public DataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( jdbcUrl );
        config.setUsername( dbUserName );
        config.setPassword( dbPassword );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        return new HikariDataSource(config);
    }


}