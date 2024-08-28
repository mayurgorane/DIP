package com.example.TenantAdmin.tenantConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class DynamicDataSource extends AbstractRoutingDataSource {

    private final Map<String, HikariDataSource> tenantDataSources = new HashMap<>();

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant();
    }

    public void setTenantDataSource(String tenantId, HikariConfig hikariConfig) {
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        tenantDataSources.put(tenantId, hikariDataSource);

        Map<Object, Object> dataSourceMap = new HashMap<>(this.getResolvedDataSources());
        dataSourceMap.put(tenantId, hikariDataSource);
        this.setTargetDataSources(dataSourceMap);
        this.afterPropertiesSet();
    }

    public DataSource getTenantDataSource(String tenantId) {
        return tenantDataSources.get(tenantId);
    }

    public void printAllConnections() {
        System.out.println("Current tenant DataSources:");
        for (Map.Entry<String, HikariDataSource> entry : tenantDataSources.entrySet()) {
            String tenantId = entry.getKey();
            HikariDataSource hikariDataSource = entry.getValue();
            System.out.println("Tenant ID: " + tenantId);
            System.out.println("URL: " + hikariDataSource.getJdbcUrl());
            System.out.println("Username: " + hikariDataSource.getUsername());
        }
    }
}
