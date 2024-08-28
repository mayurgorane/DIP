package com.example.TenantAdmin.tenantConfig;


public class TenantContext {
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenant(String tenant) {
        currentTenant.set(tenant);
        System.out.println("TenantContext set for tenant: " + tenant); // Debug log
    }

    public static String getCurrentTenant() {
        String tenant = currentTenant.get();
        System.out.println("TenantContext retrieved for tenant: " + tenant); // Debug log
        return tenant;
    }

    public static void clear() {
        currentTenant.remove();
    }
}