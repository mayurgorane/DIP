package com.example.TenantAdmin.tenantConfig;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DynamicDataSourceConfig {

    @Bean
    public DynamicDataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        // Default DataSource configuration
        HikariConfig defaultConfig = new HikariConfig();
        defaultConfig.setDriverClassName("org.postgresql.Driver");
        defaultConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/default");
        defaultConfig.setUsername("postgres");
        defaultConfig.setPassword("root");

        HikariDataSource defaultDataSource = new HikariDataSource(defaultConfig);

        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource);
        dynamicDataSource.setTargetDataSources(new HashMap<>());

        return dynamicDataSource;
    }
}