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
        dataSource.setMaximumPoolSize(5);
        dataSource.setPoolName("ReadOnlyPool");
        dataSource.setConnectionTimeout(5000);
        return dataSource;
    }
}
