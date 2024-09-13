package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.CompanyDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyServiceInterface companyServiceImplementation;  //

    @GetMapping("/register-company")
    String registerCompany(){
        return "registerCompany";
    }

    @GetMapping("/login-company")
    String loginCompany(){
        return "loginCompany";
    }

    @PostMapping("/register-company")
    public String handleCompanyRegisteration(@ModelAttribute CompanyDAO companyDAO){

        // use aspect over here

        // compare the passwords... although we are doing it with js at the view side

        Company company = Company.builder()
                .name(companyDAO.getCompanyName())
                .email(companyDAO.getCompanyEmail())
                .phonenumber(companyDAO.getCompanyPhoneNumber())
                .password(companyDAO.getCompanyPassword())
                .website(companyDAO.getCompanyWebsite())
                .build();

        companyServiceImplementation.registerCompany(company);

        return "loginCompany";

    }
}
