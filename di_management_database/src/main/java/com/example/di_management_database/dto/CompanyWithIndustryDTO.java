package com.example.di_management_database.dto;

import com.example.di_management_database.entities.Company;
import com.example.di_management_database.entities.IndustryType;

public class CompanyWithIndustryDTO {

    private Company company;
    private IndustryType industryType;

    // Constructors
    public CompanyWithIndustryDTO() {}

    public CompanyWithIndustryDTO(Company company, IndustryType industryType) {
        this.company = company;
        this.industryType = industryType;
    }

    // Getters and Setters
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public IndustryType getIndustryType() {
        return industryType;
    }

    public void setIndustryType(IndustryType industryType) {
        this.industryType = industryType;
    }
}
