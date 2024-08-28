package com.example.TenantAdmin.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "diwk_user_roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wk_user_role_id")
     private Long wkUserRoleId;

    @Column(name = "role_name")
    private String roleName;
    @Column(name = "role_type")
    private String roleType;
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

    public Long getWkUserRoleId() {
        return wkUserRoleId;
    }

    public void setWkUserRoleId(Long wkUserRoleId) {
        this.wkUserRoleId = wkUserRoleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

    @Override
    public String toString() {
        return "Role{" +
                "wkUserRoleId=" + wkUserRoleId +
                ", roleName='" + roleName + '\'' +
                ", roleType='" + roleType + '\'' +
                ", isActive=" + isActive +
                ", isRemoved=" + isRemoved +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate=" + createdDate +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", modifiedDate=" + modifiedDate +
                '}';
    }
// Getters and Setters
}
