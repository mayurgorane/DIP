package com.example.di_management_database.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dimg_company")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long com_id;

    @Column(unique = true, nullable = false)
    private UUID com_guid;
    @Column(name = "company_name")
    private String companyName;
    private String address1;
    private String address2;
    private String city;
    private String zipCode;
    private String state;
    private String country;
//    @Column(unique = true, nullable = false)
    private String email;
    private String phone_number;
//
//    @Column(unique = true)
    private String company_website_url;

    private Boolean is_active;
    private Boolean is_removed;
    private String created_by;
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    private String modified_by;
    private LocalDateTime modified_date;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Workspace workspace;


    @Column(unique = true, nullable = false,name ="com_dname")
    private String comDname;
    @Column(name = "adm_fist_name")
    private String admFirstName;
    @Column(name = "adm_last_name")
    private String admLastName;

    @Column(unique = true, nullable = false)
    private String adm_email;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Workdb workdb;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_type_id", referencedColumnName = "industry_type_id")
     private IndustryType industryType;

    public IndustryType getIndustryType() {
        return industryType;
    }

    public void setIndustryType(IndustryType industryType) {
        this.industryType = industryType;
    }

    private Boolean is_email_verified;
    private UUID verification_token;

    public Boolean getIs_email_verified() {
        return is_email_verified;
    }

    public String getAdmLastName() {
        return admLastName;
    }

    public void setAdmLastName(String admLastName) {
        this.admLastName = admLastName;
    }

    public void setIs_email_verified(Boolean is_email_verified) {
        this.is_email_verified = is_email_verified;
    }

    public UUID getVerification_token() {
        return verification_token;
    }

    public void setVerification_token(UUID verification_token) {
        this.verification_token = verification_token;
    }


    public String getAdmFirstName() {
        return admFirstName;
    }

    public void setAdmFirstName(String admFirstName) {
        this.admFirstName = admFirstName;
    }

    public String getAdm_email() {
        return adm_email;
    }

    public void setAdm_email(String adm_email) {
        this.adm_email = adm_email;
    }

    public Workdb getWorkdb() {
        return workdb;
    }

    public void setWorkdb(Workdb workdb) {
        this.workdb = workdb;
    }


    public String getComDname() {
        return comDname;
    }

    public void setComDname(String comDname) {
        this.comDname = comDname;
    }

    public Long getCom_id() {
        return com_id;
    }

    public void setCom_id(Long com_id) {
        this.com_id = com_id;
    }

    public UUID getCom_guid() {
        return com_guid;
    }

    public void setCom_guid(UUID com_guid) {
        this.com_guid = com_guid;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany_website_url() {
        return company_website_url;
    }

    public void setCompany_website_url(String company_website_url) {
        this.company_website_url = company_website_url;
    }


    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

    public Boolean getIs_removed() {
        return is_removed;
    }

    public void setIs_removed(Boolean is_removed) {
        this.is_removed = is_removed;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getModified_by() {
        return modified_by;
    }

    public void setModified_by(String modified_by) {
        this.modified_by = modified_by;
    }

    public LocalDateTime getModified_date() {
        return modified_date;
    }

    public void setModified_date(LocalDateTime modified_date) {
        this.modified_date = modified_date;
    }
}
