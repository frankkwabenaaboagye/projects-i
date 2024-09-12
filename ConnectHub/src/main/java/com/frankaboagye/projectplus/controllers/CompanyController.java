package com.frankaboagye.projectplus.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CompanyController {

    @GetMapping("/register-company")
    String registerCompany(){
        return "registerCompany";
    }
}
