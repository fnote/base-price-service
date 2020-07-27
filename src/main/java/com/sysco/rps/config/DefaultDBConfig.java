package com.sysco.rps.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;


/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 04. Jun 2020 07:19
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "defaultEntityManagerFactory",
        basePackages = {"com.sysco.rps.repository.pp"}
)
public class DefaultDBConfig {

//    @Primary
    @Bean(name = "defaultDataSource")
    @ConfigurationProperties(prefix = "resolved.db")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

//    @Primary
    @Bean(name = "defaultEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("defaultDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.sysco.rps.entity.pp")
                .persistenceUnit("ref_price_dev")
                .build();
    }

//    @Primary
    @Bean(name = "defaultTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("defaultEntityManagerFactory") EntityManagerFactory
                    entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
