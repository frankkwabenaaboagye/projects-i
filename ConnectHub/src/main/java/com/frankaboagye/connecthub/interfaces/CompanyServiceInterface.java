package com.frankaboagye.connecthub.interfaces;

import com.frankaboagye.connecthub.entities.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyServiceInterface {

    // List<Company> getAllCompanies();
    void registerCompany(Company company);

    Optional<Company> loginCompany(String email, String password);
    Optional<Company> updateCompanyProfile(Long companyId, Company companyUpdate);
}
