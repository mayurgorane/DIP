package com.example.di_management_database.dto;

import com.example.di_management_database.entities.Admin;
import com.example.di_management_database.entities.Company;
import com.example.di_management_database.entities.Workdb;

public class CompanyAdminRequest {
    private Company company;
    private Admin admin;
    private Workdb workdb;
    // Getters and Setters
    private String industryType;

    public String getIndustryType() {
        return industryType;
    }

    public void setIndustryType(String industryType) {
        this.industryType = industryType;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Workdb getWorkdb() {
        return workdb;
    }

    public void setWorkdb(Workdb workdb) {
        this.workdb = workdb;
    }
}