package com.example.di_management_database.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adm_user_id;

    @OneToOne
    @JoinColumn(name = "com_id")
    private Company company;

    private UUID adm_user_guid;
    private String first_name;
    private String last_name;
    private String adm_user_name;
    private String adm_email;
    private String password;
    private Long user_role_id;
    private Boolean is_active;
    private Boolean is_removed;
    private String created_by;
    private LocalDate created_date;
    private String modified_by;
    private LocalDate modified_date;

    public Long getAdm_user_id() {
        return adm_user_id;
    }

    public void setAdm_user_id(Long adm_user_id) {
        this.adm_user_id = adm_user_id;
    }

    public UUID getAdm_user_guid() {
        return adm_user_guid;
    }

    public void setAdm_user_guid(UUID adm_user_guid) {
        this.adm_user_guid = adm_user_guid;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAdm_user_name() {
        return adm_user_name;
    }

    public void setAdm_user_name(String adm_user_name) {
        this.adm_user_name = adm_user_name;
    }

    public String getAdm_email() {
        return adm_email;
    }

    public void setAdm_email(String adm_email) {
        this.adm_email = adm_email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getUser_role_id() {
        return user_role_id;
    }

    public void setUser_role_id(Long user_role_id) {
        this.user_role_id = user_role_id;
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

    public LocalDate getCreated_date() {
        return created_date;
    }

    public void setCreated_date(LocalDate created_date) {
        this.created_date = created_date;
    }

    public String getModified_by() {
        return modified_by;
    }

    public void setModified_by(String modified_by) {
        this.modified_by = modified_by;
    }

    public LocalDate getModified_date() {
        return modified_date;
    }

    public void setModified_date(LocalDate modified_date) {
        this.modified_date = modified_date;
    }
}
