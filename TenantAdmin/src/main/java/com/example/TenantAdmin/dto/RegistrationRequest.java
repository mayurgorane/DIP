package com.example.TenantAdmin.dto;

import com.example.TenantAdmin.entities.Company;
import com.example.TenantAdmin.entities.User;

public class RegistrationRequest {
    private Company company;
    private User user;

    // Getters and Setters
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
