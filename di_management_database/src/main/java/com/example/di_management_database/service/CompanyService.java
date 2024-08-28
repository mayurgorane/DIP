package com.example.di_management_database.service;

import com.example.di_management_database.dto.AdminWorkspace;
import com.example.di_management_database.entities.*;
import com.example.di_management_database.repository.CompanyRepository;
import com.example.di_management_database.repository.IndustryTypeRepository;
import com.example.di_management_database.repository.WorkdbRepository;
import com.example.di_management_database.repository.WorkspaceRepository;
import org.hibernate.jdbc.Work;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;


    @Autowired
    WorkdbRepository workdbRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    WorkspaceRepository workspaceRepository;


    @Autowired
    IndustryTypeRepository industryTypeRepository;

    @Transactional
    public void addCompanyAndAdmin(Company company, Workdb workdb, Admin admin, String industryType) {
        if (company == null || workdb == null) {
            throw new IllegalArgumentException("Company and Workdb cannot be null");
        }

        try {

            if (companyRepository.existsByComDname(company.getComDname())) {
                throw new IllegalArgumentException("Company display name must be unique. A company with the Com-Dname name already exists.");
            }
            if (companyRepository.existsByCompanyName(company.getCompanyName())) {
                throw new IllegalArgumentException("Company name must be unique. A company with the same name already exists.");
            }
           /* if (companyRepository.existsByEmail(company.getEmail())) {
                throw new IllegalArgumentException("Company email must be unique. A company with the same email already exists.");
            }
            if (companyRepository.existsByAdmEmail(admin.getAdm_email())) {
                throw new IllegalArgumentException("Admin email already exists");
            }
            if (companyRepository.existsByCompany_website_url(company.getCompany_website_url())) {
                throw new IllegalArgumentException("Company with the given url already exists");
            }*/

            // Find the IndustryType entity based on the provided industryType string
            Optional<IndustryType> industryTypeOptional = industryTypeRepository.findByIndustryType(industryType);
            if (industryTypeOptional.isEmpty()) {
                throw new IllegalArgumentException("Invalid industry type provided: ");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
            LocalDateTime dateTime = LocalDateTime.now(ZoneId.systemDefault());
            IndustryType foundIndustryType = industryTypeOptional.get();

            // Generate UUIDs for company and workdb
            UUID companyGuid = UUID.randomUUID();
            UUID workdbGuid = UUID.randomUUID();

            // Set the generated GUIDs
            company.setIndustryType(foundIndustryType);
            company.setCom_guid(companyGuid);
            workdb.setWorkdb_guid(workdbGuid);
            UUID verificationToken = UUID.randomUUID();

            company.setCreated_by("SUPER_ADMIN");
            company.setModified_by("SUPER_ADMIN");
            company.setIs_removed(false);
            company.setIs_active(false);

            company.setCreatedDate(LocalDateTime.parse(dateTime.format(formatter), formatter));
            company.setModified_date(LocalDateTime.parse(dateTime.format(formatter), formatter));
            company.setIs_email_verified(false);
            company.setVerification_token(verificationToken);
            company.setAdmFirstName(admin.getFirst_name());
            company.setAdmLastName(admin.getLast_name());
            company.setAdm_email(admin.getAdm_email());

            admin.setCreated_date(LocalDate.now(ZoneId.systemDefault()));
            admin.setCreated_by("SUPER_ADMIN");
            admin.setModified_by("SUPER_ADMIN");
            admin.setModified_date(LocalDate.now(ZoneId.systemDefault()));
            admin.setIs_active(true);
            admin.setIs_removed(false);


            // Save the company
            Company savedCompany = companyRepository.save(company);

            // Ensure that the company has an ID and GUID before setting it in Workdb
            Long companyId = savedCompany.getCom_id();
            UUID savedCompanyGuid = savedCompany.getCom_guid();

            if (companyId != null && savedCompanyGuid != null) {
                String companyName = savedCompany.getCompanyName();
                String databaseName = companyName + "_" + savedCompanyGuid;


                // Set values in Workdb
                workdb.setCompany(savedCompany);
                workdb.setWorkdb_name(databaseName);
                workdb.setIs_active(true);
                workdb.setIs_removed(false);
                workdb.setCreated_by("SUPER_ADMIN");
                workdb.setModified_by("SUPER_ADMIN");
                workdb.setCreated_date(LocalDateTime.parse(dateTime.format(formatter), formatter));
                workdb.setModified_date(LocalDateTime.parse(dateTime.format(formatter), formatter));

                workdbRepository.save(workdb);


                String verificationUrl = String.format("http://192.168.21.39:8080/api/users/verify-email/%s", verificationToken);
                String verificationSubject = "Verify Your Company Registration";
                String verificationText = String.format("Dear Admin,\n\n" +
                        "Your company registration has been successfully processed.\n\n" +
                        "Please verify your registration by clicking the following link:\n\n" +
                        "%s\n\n" +
                        "Best regards,\nYour Company", verificationUrl);

                // Send verification email (consider setting to a placeholder if needed)
                emailService.sendEmail(company.getAdm_email(), verificationSubject, verificationText);

            } else {
                throw new IllegalStateException("Company ID or GUID is null, cannot save Workdb.");
            }

        } catch (IllegalArgumentException e) {
            // Log and rethrow specific exception to trigger transaction rollback
            System.err.println("Illegal argument: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (DataAccessException e) {
            // Log and rethrow specific exception to trigger transaction rollback
            System.err.println("Data access error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            // Log and wrap other exceptions in a RuntimeException to trigger rollback
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("An error occurred while adding Company and Workdb", e);
        }
    }


    public String generateRandomPassword(int length) {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }


    public void createDatabase(String databaseName, Workdb workdb) throws Exception {
        String sanitizedDatabaseName = sanitizeDatabaseName(databaseName);

        // Create a new DataSource for the default database
        DriverManagerDataSource defaultDataSource = new DriverManagerDataSource();

        defaultDataSource.setDriverClassName("org.postgresql.Driver");
        defaultDataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        defaultDataSource.setUsername(workdb.getDbuser_name());
        defaultDataSource.setPassword(workdb.getPassword());

        // Create the new database using a direct connection
        try (Connection connection = defaultDataSource.getConnection(); Statement statement = connection.createStatement()) {
            String sql = "CREATE DATABASE " + sanitizedDatabaseName;
            statement.executeUpdate(sql);
        } catch (Exception e) {
            throw new Exception("Error creating database: " + e.getMessage(), e);
        }
    }

    public void createTablesInDatabase(String databaseName, Workdb workdb) throws Exception {
        String sanitizedDatabaseName = sanitizeDatabaseName(databaseName);

        DriverManagerDataSource newDatabaseDataSource = new DriverManagerDataSource();
        newDatabaseDataSource.setDriverClassName("org.postgresql.Driver");
        newDatabaseDataSource.setUrl(workdb.getServer_name() + sanitizedDatabaseName);
        newDatabaseDataSource.setUsername(workdb.getDbuser_name());
        newDatabaseDataSource.setPassword(workdb.getPassword());

        try (Connection connection = newDatabaseDataSource.getConnection(); Statement statement = connection.createStatement()) {

            // Create the role table
            String createRoleTable = "CREATE TABLE IF NOT EXISTS diwk_user_roles (" + "wk_user_role_id BIGSERIAL PRIMARY KEY, " + "role_name VARCHAR(255) NOT NULL, " + "role_type VARCHAR(255), " + "is_active BOOLEAN DEFAULT TRUE, " + "is_removed BOOLEAN DEFAULT FALSE, " + "created_by VARCHAR(255), " + "created_date TIMESTAMP, " // Use TIMESTAMP instead of DATE for accurate time tracking
                    + "modified_by VARCHAR(255), " + "modified_date TIMESTAMP" // Use TIMESTAMP instead of DATE for accurate time tracking
                    + ")";
            statement.executeUpdate(createRoleTable);

            // Insert predefined roles
            String insertRoles = "INSERT INTO diwk_user_roles (wk_user_role_id, role_name, is_active, is_removed) " + "VALUES (1, 'ADMIN', TRUE, FALSE), (2, 'USER', TRUE, FALSE) " + "ON CONFLICT (wk_user_role_id) DO NOTHING";
            statement.executeUpdate(insertRoles);

            // Create the company table
            String createCompanyTable = "CREATE TABLE IF NOT EXISTS diwk_company (" +
                    "wk_com_id BIGSERIAL PRIMARY KEY, " +
                    "com_guid UUID NOT NULL UNIQUE, " +
                    "company_name VARCHAR(255), " +
                    "address1 VARCHAR(255), " +
                    "address2 VARCHAR(255), " +
                    "city VARCHAR(255), " +
                    "zip_code VARCHAR(255), " +
                    "state VARCHAR(255), " +
                    "country VARCHAR(255), " +
                    "email VARCHAR(255), " +
                    "phone_number VARCHAR(255), " +
                    "company_website_url VARCHAR(255), " +
                    "industry_type_id BIGINT, " +
                    "atlas_user_guid UUID, " +
                    "is_active BOOLEAN DEFAULT TRUE, " +
                    "is_removed BOOLEAN DEFAULT FALSE, " +
                    "created_by VARCHAR(255), " +
                    "created_date TIMESTAMP, " +
                    "modified_by VARCHAR(255), " +
                    "modified_date TIMESTAMP, " +
                    "com_dname VARCHAR(255)" + // Added field
                    ")";

            statement.executeUpdate(createCompanyTable);

            // Create the user table with foreign key references to the company and role tables
            String createUserTable = "CREATE TABLE IF NOT EXISTS diwk_users (" +
                    "wk_user_id BIGSERIAL PRIMARY KEY, " +
                    "wk_user_guid UUID NOT NULL UNIQUE, " +
                    "first_name VARCHAR(255), " +
                    "last_name VARCHAR(255), " +
                    "wk_user_name VARCHAR(255), " +
                    "email VARCHAR(255), " +
                    "password VARCHAR(255), " +
                    "is_active BOOLEAN DEFAULT TRUE, " +
                    "is_removed BOOLEAN DEFAULT FALSE, " +
                    "created_by VARCHAR(255), " +
                    "created_date TIMESTAMP, " +
                    "modified_by VARCHAR(255), " +
                    "modified_date TIMESTAMP, " +
                    "com_guid UUID, " +
                    "wk_user_role_id BIGINT, " +
                    "is_email_verified BOOLEAN DEFAULT FALSE, " +  // New field
                    "verification_token UUID, " +                // New field
                    "CONSTRAINT fk_company FOREIGN KEY (com_guid) REFERENCES diwk_company(com_guid) ON DELETE SET NULL, " +
                    "CONSTRAINT fk_role FOREIGN KEY (wk_user_role_id) REFERENCES diwk_user_roles(wk_user_role_id) ON DELETE SET NULL" +
                    ")";
            statement.executeUpdate(createUserTable);

            // Create the workspace table
            String createWorkspaceTable = "CREATE TABLE IF NOT EXISTS diwk_workspace (" +
                    "wk_workspace_id SERIAL PRIMARY KEY, " +
                    "com_guid VARCHAR(50), " +
                    "com_workspace_guid VARCHAR(50), " +
                    "wk_user_id BIGINT, " +
                    "wk_workspace_guid UUID, " +
                    "wk_workspace_name VARCHAR(100), " +
                    "com_workspace_path VARCHAR(200), " +
                    "wk_workspace_path VARCHAR(200), " +
                    "storage_limit SMALLINT, " +
                    "is_active BOOLEAN DEFAULT TRUE, " +
                    "is_removed BOOLEAN DEFAULT FALSE, " +
                    "created_by INT, " +
                    "created_date TIMESTAMP, " +
                    "modified_by INT, " +
                    "modified_date TIMESTAMP, " +
                    "CONSTRAINT fk_user FOREIGN KEY (wk_user_id) REFERENCES diwk_users(wk_user_id) ON DELETE SET NULL" +
                    ")";
            statement.executeUpdate(createWorkspaceTable);

        } catch (Exception e) {
            throw new Exception("Error creating tables in database: " + e.getMessage(), e);
        }
    }

    public void saveCompanyAndAdmin(String databaseName, Company company, Admin admin, Workdb workdb) {
        String sanitizedDatabaseName = sanitizeDatabaseName(databaseName);

        DriverManagerDataSource newDatabaseDataSource = new DriverManagerDataSource();

        newDatabaseDataSource.setDriverClassName("org.postgresql.Driver");
        newDatabaseDataSource.setUrl(workdb.getServer_name() + sanitizedDatabaseName);
        newDatabaseDataSource.setUsername(workdb.getDbuser_name());
        newDatabaseDataSource.setPassword(workdb.getPassword());

        JdbcTemplate newJdbcTemplate = new JdbcTemplate(newDatabaseDataSource);

        try {
            // Ensure the admin's role is set to admin (role_id = 1)
            Long adminRoleId = 1L;


            UUID adminGuid = admin.getAdm_user_guid();
            UUID companyGuid = company.getCom_guid();
            String encodedPassword = passwordEncoder.encode(admin.getPassword());

            // Insert into company table
            String insertCompanySql = "INSERT INTO diwk_company " +
                    "(com_guid, company_name, address1, address2, city, zip_code, state, country, email, phone_number, company_website_url, industry_type_id, atlas_user_guid, is_active, is_removed, created_by, created_date, modified_by, modified_date, com_dname) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            newJdbcTemplate.update(insertCompanySql, companyGuid,  // com_guid
                    company.getCompanyName(),       // company_name
                    company.getAddress1(),           // address1
                    company.getAddress2(),           // address2
                    company.getCity(),               // city
                    company.getZipCode(),            // zip_code
                    company.getState(),              // state
                    company.getCountry(),            // country
                    company.getEmail(),              // email
                    company.getPhone_number(),       // phone_number
                    company.getCompany_website_url(),// company_website_url
                    company.getIndustryType().getIndustry_type_id(),   // industry_type_id
                    null,                            // atlas_user_guid (if null)
                    company.getIs_active(),           // is_active
                    company.getIs_removed(),          // is_removed
                    company.getCreated_by(),          // created_by
                    company.getCreatedDate(),        // created_date
                    company.getModified_by(),         // modified_by
                    company.getModified_date(),
                    company.getComDname()
            );

            // Insert into user table
            String insertAdminSql = "INSERT INTO diwk_users " +
                    "(wk_user_guid, first_name, last_name, wk_user_name, email, password, is_active, is_removed, created_by, created_date, modified_by, modified_date, wk_user_role_id, com_guid, is_email_verified, verification_token) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE, NULL)";

            newJdbcTemplate.update(insertAdminSql,

                    adminGuid,
                    admin.getFirst_name(),
                    admin.getLast_name(),
                    admin.getAdm_user_name(),
                    admin.getAdm_email(),
                    encodedPassword,
                    admin.getIs_active(),
                    admin.getIs_removed(),
                    admin.getCreated_by(),
                    admin.getCreated_date(),
                    admin.getModified_by(),
                    admin.getModified_date(),
                    adminRoleId,
                    companyGuid
                    // `is_email_verified` is set to FALSE and `verification_token` is set to NULL directly in the SQL statement
            );


            // Insert into workspace table
            String insertWorkspaceSql = "INSERT INTO diwk_workspace " +
                    "(com_guid, com_workspace_guid, wk_user_id, wk_workspace_guid, wk_workspace_name, com_workspace_path, wk_workspace_path, storage_limit, is_active, is_removed, created_by, created_date, modified_by, modified_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            // Assume workspace object is created with appropriate values
            AdminWorkspace workspace = createWorkspaces(company, admin.getFirst_name());

            UUID workspaceGuid = UUID.randomUUID();
            newJdbcTemplate.update(insertWorkspaceSql,
                    companyGuid,                 // com_guid
                    workspace.getWorkspace().getWorkspace_guid(), // com_workspace_guid
                    1,       // wk_user_id
                    workspaceGuid,  // wk_workspace_guid
                    workspace.getWorkspace().getWorkspace_name(),  // wk_workspace_name
                    workspace.getCompanyWorkspacePath(), // com_workspace_path
                    workspace.getWorkspace().getWorkspace_path(),  // wk_workspace_path
                    workspace.getWorkspace().getStorage_limit(),      // storage_limit
                    workspace.getWorkspace().getIs_active(),          // is_active
                    workspace.getWorkspace().getIs_removed(),         // is_removed
                    workspace.getWorkspace().getCreated_by(),         // created_by
                    workspace.getWorkspace().getCreated_date(),       // created_date
                    workspace.getWorkspace().getModified_by(),        // modified_by
                    workspace.getWorkspace().getModified_date()       // modified_date
            );

        } catch (Exception e) {
            // Log the exception and rethrow it
            System.err.println("Error saving company and admin to database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error saving company and admin to database: " + e.getMessage(), e);
        }
    }

    public String sanitizeDatabaseName(String databaseName) {
        // Sanitize the database name
        String sanitizedDatabaseName = databaseName.replaceAll("[^a-zA-Z0-9_]", "_");

        // Ensure the sanitized database name is not empty and does not exceed the length limit
        if (sanitizedDatabaseName.length() == 0 || sanitizedDatabaseName.length() > 63) {
            throw new IllegalArgumentException("Invalid database name: " + databaseName);
        }

        return sanitizedDatabaseName;
    }

    @Transactional
    public String verifyEmailAndCreateAdmin(UUID token) {
        Company company = companyRepository.findByVerification_token(token);
        if (company == null) {
            return "Invalid verification token.";
        }

        company.setIs_email_verified(true);
        company.setIs_active(true);
        company.setVerification_token(null);
        companyRepository.save(company);

        Optional<Workdb> workdbOptional = workdbRepository.findByCom_guid(company.getCom_guid());
        if (!workdbOptional.isPresent()) {
            return "Workdb not found.";
        }

        Workdb workdb = workdbOptional.get();
        String companyName = company.getCompanyName();
        String databaseName = companyName + "_" + company.getCom_guid();

        UUID adminGuid = UUID.randomUUID();
        String adminUserName = company.getAdmFirstName() + "." + company.getAdmLastName() + "@" + company.getComDname();
        String randomPassword = generateRandomPassword(8);
        LocalDate now = LocalDate.now();

        Admin admin = new Admin();
        admin.setAdm_user_guid(adminGuid);
        admin.setFirst_name(company.getAdmFirstName());
        admin.setLast_name(company.getAdmLastName());
        admin.setAdm_user_name(adminUserName);
        admin.setAdm_email(company.getAdm_email());
        admin.setPassword(randomPassword);
        admin.setUser_role_id(1L);
        admin.setIs_active(true);
        admin.setIs_removed(false);
        admin.setCreated_by("SUPER_ADMIN");
        admin.setModified_by("SUPER_ADMIN");
        admin.setCreated_date(now);
        admin.setModified_date(now);

        try {
            createDatabase(databaseName, workdb);
            createTablesInDatabase(databaseName, workdb);
            saveCompanyAndAdmin(databaseName, company, admin, workdb);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String emailSubject = "Your Account Information";
        String emailText = String.format("Dear %s %s,\n\n" +
                        "Your email has been verified successfully.\n\n" +
                        "Please log in using your username and password given below.\n\n" +
                        "Login URL: http://192.168.21.39:9090/api/auth/login\n" +
                        "Username: %s\n" +

                        "Password: %s\n\n" +
                        "Best regards,\nYour Company",
                company.getAdmFirstName(),
                company.getAdmLastName(),
                adminUserName, // Assuming this is the username

                randomPassword);

        emailService.sendEmail(company.getAdm_email(), emailSubject, emailText);

        return "Email verified and database created successfully.";
    }


    // Method to create workspace directories
    public AdminWorkspace createWorkspaces(Company company, String adminName) {
        // Define the company's workspace path
        String companyWorkspacePath = "D:\\workspace\\" + company.getCompanyName() + "_" + company.getCom_guid();

        // Create the company's workspace directory if it doesn't exist
        File companyDirectory = new File(companyWorkspacePath);
        if (!companyDirectory.exists()) {
            boolean created = companyDirectory.mkdirs();
            if (created) {
                System.out.println("Company workspace directory created successfully at: " + companyWorkspacePath);
            } else {
                throw new RuntimeException("Failed to create company workspace directory at: " + companyWorkspacePath);
            }
        } else {
            System.out.println("Company workspace directory already exists at: " + companyWorkspacePath);
        }

        // Create the admin workspace directory
        String adminWorkspacePath = companyWorkspacePath + "\\" + adminName;
        File adminDirectory = new File(adminWorkspacePath);
        if (!adminDirectory.exists()) {
            boolean created = adminDirectory.mkdirs();
            if (created) {
                System.out.println("Admin workspace directory created successfully at: " + adminWorkspacePath);
            } else {
                throw new RuntimeException("Failed to create admin workspace directory at: " + adminWorkspacePath);
            }
        } else {
            System.out.println("Admin workspace directory already exists at: " + adminWorkspacePath);
        }

        // Get the current LocalDateTime
        LocalDateTime now = LocalDateTime.now();

        // Save the company's workspace details in the database
        Workspace companyWorkspace = new Workspace();
        companyWorkspace.setCompany(company);
        companyWorkspace.setWorkspace_guid(UUID.randomUUID());
        companyWorkspace.setWorkspace_name(company.getCompanyName());
        companyWorkspace.setWorkspace_path(companyWorkspacePath);
        companyWorkspace.setStorage_limit((short) 100);  // Example storage limit
        companyWorkspace.setIs_active(true);
        companyWorkspace.setIs_removed(false);
        companyWorkspace.setCreated_by(1);  // Example created_by
        companyWorkspace.setCreated_date(now);
        companyWorkspace.setModified_by(1);  // Example modified_by
        companyWorkspace.setModified_date(now);
        Workspace savedWorkSpace = workspaceRepository.save(companyWorkspace);

        //////////////
        // Create and initialize AdminWorkspace
        AdminWorkspace adminWorkspace = new AdminWorkspace();
        Workspace workspace = new Workspace();


        workspace.setCompany(company);
        workspace.setWorkspace_guid(savedWorkSpace.getWorkspace_guid());
        workspace.setWorkspace_name(adminName);
        workspace.setWorkspace_path(adminWorkspacePath);
        workspace.setStorage_limit((short) 10);  // Example storage limit
        workspace.setIs_active(true);
        workspace.setIs_removed(false);
        workspace.setCreated_by(1);  // Example created_by
        workspace.setCreated_date(now);
        workspace.setModified_by(1);  // Example modified_by
        workspace.setModified_date(now);
        adminWorkspace.setWorkspace(workspace);
        adminWorkspace.setCompanyWorkspacePath(companyWorkspacePath);

        // Save the workspaces to the database...

        return adminWorkspace;

    }

    public Page<Company> getAllCompanies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        return companyRepository.findAll(pageable);
    }


    public Page<Company> searchCompaniesByName(String name, int page, int size, String sortBy, String sortDirection) {
        Pageable pageable;

        if ("industryType".equalsIgnoreCase(sortBy)) {
            // Use sorting by industry type name
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), "industryType.industryType"));
            return companyRepository.searchByCompanyName(name, pageable);
        } else {
            // Use standard sorting
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
            return companyRepository.searchByCompanyName(name, pageable);
        }
    }

    public Company toggleCompanyStatus(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

        // Toggle the isActive status
        boolean newStatus = !company.getIs_active();
        company.setIs_active(newStatus);

        // Save the updated company
        companyRepository.save(company);

        return company;
    }
}