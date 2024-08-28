package com.example.TenantAdmin.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "diwk_workspace")
public class WorkSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wk_workspace_id")
    private Long id;

    @Column(name = "com_guid", length = 50)
    private String comGuid;

    @Column(name = "com_workspace_guid", length = 50)
    private String comWorkspaceGuid;

    @OneToOne
    @JoinColumn(name = "wk_user_id",referencedColumnName ="wk_user_id")
    private User user;

    @Column(name = "wk_workspace_guid")
    private UUID workspaceGuid;

    @Column(name = "wk_workspace_name", length = 100)
    private String workspaceName;

    @Column(name = "com_workspace_path", length = 200)
    private String comWorkspacePath;

    @Column(name = "wk_workspace_path", length = 200)
    private String workspacePath;

    @Column(name = "storage_limit")
    private Short storageLimit;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_removed")
    private Boolean isRemoved = false;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "modified_by")
    private Integer modifiedBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComGuid() {
        return comGuid;
    }

    public void setComGuid(String comGuid) {
        this.comGuid = comGuid;
    }

    public String getComWorkspaceGuid() {
        return comWorkspaceGuid;
    }

    public void setComWorkspaceGuid(String comWorkspaceGuid) {
        this.comWorkspaceGuid = comWorkspaceGuid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UUID getWorkspaceGuid() {
        return workspaceGuid;
    }

    public void setWorkspaceGuid(UUID workspaceGuid) {
        this.workspaceGuid = workspaceGuid;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getComWorkspacePath() {
        return comWorkspacePath;
    }

    public void setComWorkspacePath(String comWorkspacePath) {
        this.comWorkspacePath = comWorkspacePath;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public void setWorkspacePath(String workspacePath) {
        this.workspacePath = workspacePath;
    }

    public Short getStorageLimit() {
        return storageLimit;
    }

    public void setStorageLimit(Short storageLimit) {
        this.storageLimit = storageLimit;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsRemoved() {
        return isRemoved;
    }

    public void setIsRemoved(Boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}