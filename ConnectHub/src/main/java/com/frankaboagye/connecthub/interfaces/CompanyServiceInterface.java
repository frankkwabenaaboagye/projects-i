package com.frankaboagye.connecthub.interfaces;

import com.frankaboagye.connecthub.daos.CompanyUpdateDAO;
import com.frankaboagye.connecthub.dtos.CompanyDTO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.entities.Project;
import com.frankaboagye.connecthub.entities.ProjectDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

//
//import com.frankaboagye.connecthub.dtos.CompanyDTO;
//import com.frankaboagye.connecthub.entities.Company;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.Optional;
//
public interface CompanyServiceInterface {

    // List<Company> getAllCompanies();
    void registerCompany(Company company);
    Optional<Company> loginCompany(String email, String password);
    Optional<Company> getCompanyById(Long companyId);
    Company updateCompany(Long companyId, CompanyUpdateDAO companyUpdateDAO, boolean updateFile, MultipartFile companyPhotoFile);
    void postAJob(Job job);
    void postAProject(Project project, ProjectDocument projectDocument);
}