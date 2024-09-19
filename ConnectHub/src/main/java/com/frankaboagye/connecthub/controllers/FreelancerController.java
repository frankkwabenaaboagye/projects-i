package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.FreelancerDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FreelancerController {

    @GetMapping("/register-freelancer")
    public String registerFreelancer(){
        return "registerFreelancer";
    }

    @PostMapping("/handle-register-freelancer")
    public String handleFreelancerRegistration(
            @ModelAttribute FreelancerDAO freelancerDAO,
            @RequestParam("freelancerPhotoFile") MultipartFile freelancerPhotoFile,
            ModelMap modelMap
    ){

        return null;

    }
}
