package com.example.TenantAdmin.entities;


import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "diwk_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wk_user_id")
    private Long wkUserId;

    @Column(nullable = false, unique = true,name ="wk_user_guid")
    private UUID wkUserGuid;

    @ManyToOne
    @JoinColumn(name = "com_guid", referencedColumnName = "com_guid")

    private Company company;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "wk_user_name")
    private String wkUserName;
    @Column(name = "email",unique = true)
    private String email;
    @Column(name = "password")
    private String password;

    @ManyToOne
    @JoinColumn(name = "wk_user_role_id", referencedColumnName = "wk_user_role_id")
    private Role role;

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
    @Column(name = "is_email_verified")
    private Boolean is_email_verified;
    @Column(name = "verification_token")
    private UUID verification_token;

    public Boolean getIs_email_verified() {
        return is_email_verified;
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

    public Long getWkUserId() {
        return wkUserId;
    }

    public void setWkUserId(Long wkUserId) {
        this.wkUserId = wkUserId;
    }

    public UUID getWkUserGuid() {
        return wkUserGuid;
    }

    public void setWkUserGuid(UUID wkUserGuid) {
        this.wkUserGuid = wkUserGuid;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getRemoved() {
        return isRemoved;
    }

    public void setRemoved(Boolean removed) {
        isRemoved = removed;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWkUserName() {
        return wkUserName;
    }

    public void setWkUserName(String wkUserName) {
        this.wkUserName = wkUserName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        return "User{" +
                "wkUserId=" + wkUserId +
                ", wkUserGuid=" + wkUserGuid +
                ", company=" + company +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", wkUserName='" + wkUserName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                ", isRemoved=" + isRemoved +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate=" + createdDate +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", modifiedDate=" + modifiedDate +
                '}';
    }
}