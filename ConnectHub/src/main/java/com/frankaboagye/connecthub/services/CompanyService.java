package com.frankaboagye.connecthub.services;

import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService implements CompanyServiceInterface {

    private final CompanyRepository companyRepository;

    @Override
    public void registerCompany(Company company) {
        // use aop here
        System.out.println("Registering company " + company.getName());
        companyRepository.save(company);
    }
}
