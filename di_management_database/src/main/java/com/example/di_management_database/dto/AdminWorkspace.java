package com.example.di_management_database.dto;

import com.example.di_management_database.entities.Workspace;

public class AdminWorkspace {

    private Workspace workspace;
    private String companyWorkspacePath;


    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public String getCompanyWorkspacePath() {
        return companyWorkspacePath;
    }

    public void setCompanyWorkspacePath(String companyWorkspacePath) {
        this.companyWorkspacePath = companyWorkspacePath;
    }
}
