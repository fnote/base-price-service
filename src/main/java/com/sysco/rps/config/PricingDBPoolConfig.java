package com.sysco.rps.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/13/20 Time: 12:54 PM
 */
@Configuration
public class PricingDBPoolConfig {

    @Value("${pricing.db.username}")
    private String dbUserName;
    @Value("${pricing.db.password}")
    private String dbPassword;
    @Value("${pricing.db.jdbcUrl}")
    private String jdbcUrl;
    @Value("${pricing.db.max.pool.size}")
    private String maxPoolSize;

    @Bean(name = "pricingDataSource")
    public DataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( jdbcUrl );
        config.setUsername( dbUserName );
        config.setPassword( dbPassword );
        config.setMaximumPoolSize(getPoolSize(maxPoolSize));
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        return new HikariDataSource(config);
    }

    private int getPoolSize(String value){
        return StringUtils.isEmpty(value) ? 1 : Integer.parseInt(value);
    }

}
