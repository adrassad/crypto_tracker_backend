package com.adrassad.cryprice.config;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import jakarta.persistence.EntityManagerFactory;


/**
 * Конфигурация второй datasource для crypto-таблицы.
 * Репозитории crypto должны быть в пакете com.adrassad.cryptotracker.crypto.repository
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.adrassad.cryptotracker.crypto.repository",
        entityManagerFactoryRef = "cryptoEntityManager",
        transactionManagerRef = "cryptoTransactionManager"
)
public class CryptoDataSourceConfig {

    @Bean(name = "cryptoDataSource")
    @ConfigurationProperties(prefix = "crypto-datasource")
    public DataSource cryptoDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "cryptoEntityManager")
    public LocalContainerEntityManagerFactoryBean cryptoEntityManager(EntityManagerFactoryBuilder builder,
                                                                      @Qualifier("cryptoDataSource") DataSource ds) {
        return builder
                .dataSource(ds)
                .packages("com.adrassad.cryptotracker.crypto.entity")
                .persistenceUnit("crypto")
                .build();
    }

    @Bean(name = "cryptoTransactionManager")
    public JpaTransactionManager cryptoTransactionManager(@Qualifier("cryptoEntityManager") EntityManagerFactory emf) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        return tm;
    }
}