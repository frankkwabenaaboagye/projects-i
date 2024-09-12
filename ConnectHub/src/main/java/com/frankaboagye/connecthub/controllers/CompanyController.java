package com.frankaboagye.connecthub.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CompanyController {

    @GetMapping("/register-company")
    String registerCompany(){
        return "registerCompany";
    }

    @GetMapping("/login-company")
    String loginCompany(){
        return "loginCompany";
    }

    @PostMapping("/register-company")
    public String handleCompanyRegisteration(){}
}
