package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.CompanyDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

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

    @PostMapping("/login-company")
    public String handleCompanyLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model,
            HttpSession session
    ){

        Optional<Company> companyData = companyServiceImplementation.loginCompany(email, password);
        if(companyData.isPresent()){
            Company company = companyData.get();
            model.addAttribute("companyId", company.getId());
            model.addAttribute("companyName", company.getName());
            model.addAttribute("companyEmail", company.getEmail());
            model.addAttribute("companyPhoneNumber", company.getPhonenumber());
            model.addAttribute("companyWebsite", company.getWebsite());
            model.addAttribute("companyPictureName", company.getProfilepicturename());

            session.setAttribute("SessionData",email);
            return "welcome";

        }
        model.addAttribute("error", "Login Failed, Try Again");
        return "loginCompany";

    }

}
