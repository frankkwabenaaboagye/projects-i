package com.frankaboagye.connecthub.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
}
