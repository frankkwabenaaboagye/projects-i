package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.CompanyDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class CompanyController {

    // note the use of interfaces here
    private final CompanyServiceInterface companyServiceImplementation;
    private final StorageServiceInterface storageServiceImplementation; // it will use the FileSystemStorageService .. since that is what has been configured

    @GetMapping("/register-company")
    String registerCompany(){
        return "registerCompany";
    }

    @GetMapping("/login-company")
    String loginCompany(){
        return "loginCompany";
    }

    @PostMapping("/register-company")
    public String handleCompanyRegisteration(@ModelAttribute CompanyDAO companyDAO, @RequestParam("companyPhotoFile") MultipartFile companyPhotoFile){

        // use aspect over here
            // we can do some serious checks
            // logging


        // compare the passwords... although we are doing it with js at the view side

        storageServiceImplementation.store(companyPhotoFile);

        Company company = Company.builder()
                .name(companyDAO.getCompanyName())
                .email(companyDAO.getCompanyEmail())
                .phonenumber(companyDAO.getCompanyPhoneNumber())
                .password(companyDAO.getCompanyPassword())
                .website(companyDAO.getCompanyWebsite())
                .profilepicturename(companyPhotoFile.getOriginalFilename())
                .build();

        companyServiceImplementation.registerCompany(company);

        return "loginCompany";

    }
}
