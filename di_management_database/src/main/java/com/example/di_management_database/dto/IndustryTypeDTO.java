package com.example.di_management_database.dto;

public class IndustryTypeDTO {

    private String industryTypeName;

    public IndustryTypeDTO(String industryTypeName) {
        this.industryTypeName = industryTypeName;
    }

    public String getIndustryTypeName() {
        return industryTypeName;
    }

    public void setIndustryTypeName(String industryTypeName) {
        this.industryTypeName = industryTypeName;
    }
}