package com.frankaboagye.connecthub.interfaces;

import com.frankaboagye.connecthub.dtos.CompanyDTO;
import com.frankaboagye.connecthub.entities.Company;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface CompanyServiceInterface {

    // List<Company> getAllCompanies();
    void registerCompany(Company company);

    Optional<Company> loginCompany(String email, String password);
    Company updateCompany(Long companyId, CompanyDTO companyDTO, boolean updateFile,MultipartFile companyPhotoFile);
}