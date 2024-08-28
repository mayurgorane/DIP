package com.example.TenantAdmin.entities;


import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "diwk_company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wk_com_id")
    private Long wkComId;
    @Column(nullable = false, unique = true, name = "com_guid")
    private UUID comGuid;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "address1")
    private String address1;
    private String address2;
    private String city;
    @Column(name = "zip_code")
    private String zipCode;
    private String state;
    private String country;
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "company_website_url")
    private String companyWebsiteUrl;
    @Column(name = "industry_type_id")
    private Long industryTypeId;
    @Column(name = "atlas_user_guid")
    private UUID atlasUserGuid;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "is_removed")
    private Boolean isRemoved;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "modified_by")
    private String modifiedBy;
    @Column(name = "modified_date")
    private Date modifiedDate;

    private String com_dname;

    public String getCom_dname() {
        return com_dname;
    }

    public void setCom_dname(String com_dname) {
        this.com_dname = com_dname;
    }

    public Long getWkComId() {
        return wkComId;
    }

    public void setWkComId(Long wkComId) {
        this.wkComId = wkComId;
    }

    public UUID getComGuid() {
        return comGuid;
    }

    public void setComGuid(UUID comGuid) {
        this.comGuid = comGuid;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCompanyWebsiteUrl() {
        return companyWebsiteUrl;
    }

    public void setCompanyWebsiteUrl(String companyWebsiteUrl) {
        this.companyWebsiteUrl = companyWebsiteUrl;
    }

    public Long getIndustryTypeId() {
        return industryTypeId;
    }

    public void setIndustryTypeId(Long industryTypeId) {
        this.industryTypeId = industryTypeId;
    }

    public UUID getAtlasUserGuid() {
        return atlasUserGuid;
    }

    public void setAtlasUserGuid(UUID atlasUserGuid) {
        this.atlasUserGuid = atlasUserGuid;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getRemoved() {
        return isRemoved;
    }

    public void setRemoved(Boolean removed) {
        isRemoved = removed;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}