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
        entityManagerFactoryRef = "entityManagerFactory",
        basePackages = { "com.sysco.rps.repository.refpricing" }
)
public class PricingDBConfig {

    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "pricing.db")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.sysco.rps.entity.refpricing")
                .persistenceUnit("reference_pricing")
                .build();
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory
                    entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
//
//    private void test() {
//        entityManagerFactory().cre
//    }
//
//    @Bean
//    public MyService myTransactionalService(DomainRepository domainRepository) {
//        return new MyServiceImpl(domainRepository);
//    }
//
//    @Bean
//    public DomainRepository domainRepository(JdbcTemplate template){
//        return new JpaAndJdbcDomainRepository(template);
//    }
//
//    @Bean
//    public JdbcTemplate jdbcTemplate(DataSource dataSource){
//        JdbcTemplate template = new JdbcTemplate(dataSource);
//        return template;
//    }
}
