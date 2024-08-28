package com.example.di_management_database.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;

@Entity
@Table(name = "dimg_workdb")
public class Workdb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workdb_id;


    private UUID workdb_guid;
    private UUID workspace_guid;
    private String server_name;
    private String workdb_name;
    private String dbuser_name;
    private String password;
    private Integer port;
    private Boolean is_active;
    private Boolean is_removed;
    private String created_by;
    private LocalDateTime created_date;
    private String modified_by;
    private LocalDateTime modified_date;

    @OneToOne
    @JoinColumn(name = "com_guid", referencedColumnName = "com_guid")
    @JsonBackReference
    private Company company;


    public Long getWorkdb_id() {
        return workdb_id;
    }

    public void setWorkdb_id(Long workdb_id) {
        this.workdb_id = workdb_id;
    }


    public UUID getWorkdb_guid() {
        return workdb_guid;
    }

    public void setWorkdb_guid(UUID workdb_guid) {
        this.workdb_guid = workdb_guid;
    }

    public UUID getWorkspace_guid() {
        return workspace_guid;
    }

    public void setWorkspace_guid(UUID workspace_guid) {
        this.workspace_guid = workspace_guid;
    }

    public String getServer_name() {
        return server_name;
    }

    public void setServer_name(String server_name) {
        this.server_name = server_name;
    }

    public String getWorkdb_name() {
        return workdb_name;
    }

    public void setWorkdb_name(String workdb_name) {
        this.workdb_name = workdb_name;
    }

    public String getDbuser_name() {
        return dbuser_name;
    }

    public void setDbuser_name(String dbuser_name) {
        this.dbuser_name = dbuser_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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


    public LocalDateTime getCreated_date() {
        return created_date;
    }

    public void setCreated_date(LocalDateTime created_date) {
        this.created_date = created_date;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}