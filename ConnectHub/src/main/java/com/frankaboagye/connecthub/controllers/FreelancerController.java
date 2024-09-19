package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.FreelancerDAO;
import com.frankaboagye.connecthub.entities.Freelancer;
import com.frankaboagye.connecthub.interfaces.FreelancerServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class FreelancerController {

    private final FreelancerServiceInterface freelancerServiceImplementation;

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

        // will make the password check better
        if(!freelancerDAO.getFreelancerPassword().equals(freelancerDAO.getFreelancerConfirmPassword())){
            modelMap.addAttribute("message", "Passwords do not match");
            return "registerFreelancer";
        }

        String a = "";

        // do the conversion in the helper class
        Freelancer freelancer = Freelancer.builder()
                .email(freelancerDAO.getEmail())
                .password(freelancerDAO.getFreelancerPassword())
                .build();

        freelancerServiceImplementation.registerFreelanceer();

        return null;

    }
}
