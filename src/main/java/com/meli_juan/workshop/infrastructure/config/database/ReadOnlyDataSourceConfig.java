package com.meli_juan.workshop.infrastructure.config.database;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class ReadOnlyDataSourceConfig {

    @Value("${spring.datasource.readonly.url}")
    private String url;

    @Value("${spring.datasource.readonly.username}")
    private String username;

    @Value("${spring.datasource.readonly.password}")
    private String password;

    @Bean(name = "readOnlyDataSource")
    public DataSource readOnlyDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setReadOnly(true);
        final int MAXIMUM_POOL_SIZE = 5;
        dataSource.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        dataSource.setPoolName("ReadOnlyPool");
        final int CONNECTION_TIMEOUT = 5000;
        dataSource.setConnectionTimeout(CONNECTION_TIMEOUT);
        return dataSource;
    }
}
