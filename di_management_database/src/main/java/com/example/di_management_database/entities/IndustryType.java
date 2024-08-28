package com.example.di_management_database.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "industry_type")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class IndustryType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long industry_type_id;

    @Column(length = 50, nullable = false,name = "industry_type")
    private String industryType;

    private Boolean is_active;
    private Boolean is_removed;
    private Integer created_by;
    private LocalDateTime created_date;
    private Integer modified_by;
    private LocalDateTime modified_date;


/*
    @OneToMany(mappedBy = "industryType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Company> companies;
*/

    // Getters and Setters
    public Long getIndustry_type_id() {
        return industry_type_id;
    }

    public void setIndustry_type_id(Long industry_type_id) {
        this.industry_type_id = industry_type_id;
    }


    public String getIndustryType() {
        return industryType;
    }

    public void setIndustryType(String industryType) {
        this.industryType = industryType;
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

    public Integer getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Integer created_by) {
        this.created_by = created_by;
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

    public LocalDateTime getModified_date() {
        return modified_date;
    }

    public void setModified_date(LocalDateTime modified_date) {
        this.modified_date = modified_date;
    }
}