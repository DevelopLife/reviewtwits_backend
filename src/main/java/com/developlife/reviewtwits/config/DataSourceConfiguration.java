package com.developlife.reviewtwits.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * @author ghdic
 * @since 2023/03/29
 */
@Configuration
@Profile("!test")
public class DataSourceConfiguration {

    private static final String SOURCE_SERVER = "SOURCE";
    private static final String REPLICA_SERVER = "REPLICA";
//    private final JpaProperties jpaProperties;


    @Bean
    @Qualifier(SOURCE_SERVER)
    @ConfigurationProperties(prefix = "spring.datasource.source")
    public DataSource sourceDataSource() {
        return DataSourceBuilder.create()
            .build();
    }

    @Bean
    @Qualifier(REPLICA_SERVER)
    @ConfigurationProperties(prefix = "spring.datasource.replica")
    public DataSource replicaDataSource() {
        return DataSourceBuilder.create()
            .build();
    }

    @Bean
    public DataSource routingDataSource(
        @Qualifier(SOURCE_SERVER) DataSource sourceDataSource, // (1)
        @Qualifier(REPLICA_SERVER) DataSource replicaDataSource
    ) {
        RoutingDataSource routingDataSource = new RoutingDataSource(); // (2)

        HashMap<Object, Object> dataSourceMap = new HashMap<>(); // (3)
        dataSourceMap.put("source", sourceDataSource);
        dataSourceMap.put("replica", replicaDataSource);

        routingDataSource.setTargetDataSources(dataSourceMap); // (4)
        routingDataSource.setDefaultTargetDataSource(sourceDataSource); // (5)

        return routingDataSource;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        DataSource determinedDataSource = routingDataSource(sourceDataSource(), replicaDataSource());
        return new LazyConnectionDataSourceProxy(determinedDataSource);
    }

    // JPA 에서 사용할 entityManager 설정
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//        EntityManagerFactoryBuilder entityManagerFactoryBuilder = createEntityManagerFactoryBuilder(jpaProperties);
//        return entityManagerFactoryBuilder.dataSource(dataSource()).packages("com.developlife.reviewtwits").build();
//    }
//
//    private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(JpaProperties jpaProperties) {
//        AbstractJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        // jpaProperties는 properties에 있는 jpa.properties를 의미
//        return new EntityManagerFactoryBuilder(vendorAdapter, jpaProperties.getProperties(), null);
//    }
//
//
//    // JPA 에서 사용할 TransactionManager 설정
//    @Bean
//    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
//        JpaTransactionManager tm = new JpaTransactionManager();
//        tm.setEntityManagerFactory(entityManagerFactory);
//        return tm;
//    }
//
//    // jdbcTemplate 세팅
//    @Bean
//    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
//        return new JdbcTemplate(dataSource);
//    }
}
