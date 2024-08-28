package com.example.di_management_database.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dimg_workspace")
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workspace_id;

    @Column(nullable = false)
    private UUID workspace_guid;

    private String workspace_name;

    @Column(length = 200)
    private String workspace_path;

    private Short storage_limit;
    private Boolean is_active;
    private Boolean is_removed;
    private Integer created_by;
    private LocalDateTime created_date;
    private Integer modified_by;
    private LocalDateTime modified_date;

    @OneToOne
    @JoinColumn(name = "com_guid", referencedColumnName = "com_guid", nullable = false)
    @JsonBackReference
    private Company company;

    public Long getWorkspace_id() {
        return workspace_id;
    }

    public void setWorkspace_id(Long workspace_id) {
        this.workspace_id = workspace_id;
    }

    public UUID getWorkspace_guid() {
        return workspace_guid;
    }

    public void setWorkspace_guid(UUID workspace_guid) {
        this.workspace_guid = workspace_guid;
    }

    public String getWorkspace_path() {
        return workspace_path;
    }

    public void setWorkspace_path(String workspace_path) {
        this.workspace_path = workspace_path;
    }

    public String getWorkspace_name() {
        return workspace_name;
    }

    public void setWorkspace_name(String workspace_name) {
        this.workspace_name = workspace_name;
    }

    public Short getStorage_limit() {
        return storage_limit;
    }

    public void setStorage_limit(Short storage_limit) {
        this.storage_limit = storage_limit;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

    public Integer getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Integer created_by) {
        this.created_by = created_by;
    }

    public Boolean getIs_removed() {
        return is_removed;
    }

    public void setIs_removed(Boolean is_removed) {
        this.is_removed = is_removed;
    }

    public LocalDateTime getCreated_date() {
        return created_date;
    }

    public void setCreated_date(LocalDateTime created_date) {
        this.created_date = created_date;
    }

    public Integer getModified_by() {
        return modified_by;
    }

    public void setModified_by(Integer modified_by) {
        this.modified_by = modified_by;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public LocalDateTime getModified_date() {
        return modified_date;
    }

    public void setModified_date(LocalDateTime modified_date) {
        this.modified_date = modified_date;
    }
    // Getters and Setters
}