package com.sysco.payplus.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/13/20
 * Time: 12:54 PM
 */
@Configuration
public class DataSourceConfig {
    private static HikariConfig config = new HikariConfig("/datasource.properties");
    @Bean
    public DataSource getDataSource() {
        return new HikariDataSource(config);
    }


}