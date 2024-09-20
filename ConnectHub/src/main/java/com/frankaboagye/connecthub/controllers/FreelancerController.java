package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.FreelancerDAO;
import com.frankaboagye.connecthub.dtos.CompanyDTO;
import com.frankaboagye.connecthub.dtos.FreelancerDTO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Freelancer;
import com.frankaboagye.connecthub.enums.Gender;
import com.frankaboagye.connecthub.interfaces.FreelancerServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import com.frankaboagye.connecthub.repositories.FreelancerRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class FreelancerController {

    private final FreelancerServiceInterface freelancerServiceImplementation;
    private final StorageServiceInterface storageServiceImplementation;
    private final FreelancerRepository freelancerRepository;

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
            return "redirect:/register-freelancer";
        }

        // do the conversion in the helper class

        // date
        // yyyy-mm-dd
        String stringDOB = freelancerDAO.getDateOfBirth();
        int year = Integer.parseInt(stringDOB.substring(0, 4));
        int month = Integer.parseInt(stringDOB.substring(5, 7));
        int day = Integer.parseInt(stringDOB.substring(8, 10));

        LocalDate dob = LocalDate.of(year, month, day);

        Double chargeRate = Double.parseDouble(freelancerDAO.getBasicCharge());

        Gender freelancerGender = Gender.valueOf(freelancerDAO.getGender().toUpperCase());

        Freelancer freelancer = Freelancer.builder()
                .fullName(freelancerDAO.getFullName())
                .email(freelancerDAO.getEmail())
                .dateOfBirth(dob)
                .gender(freelancerGender)
                .linkedin(freelancerDAO.getLinkedin())
                .phoneNumber(freelancerDAO.getPhoneNumber())
                .education(freelancerDAO.getEducation())
                .basicCharge(chargeRate)
                .profilepicturename(freelancerPhotoFile.getOriginalFilename())
                .skills(freelancerDAO.getSkills())
                .password(freelancerDAO.getFreelancerPassword())
                .build();

        storageServiceImplementation.store(freelancerPhotoFile);
        freelancerServiceImplementation.registerFreelanceer(freelancer);

        modelMap.addAttribute("message", "Freelancer registered successfully");
        return "redirect:/login-freelancer";

    }


    @GetMapping("/login-freelancer")
    public String freelancerLogin(){
        return "loginFreelancer";
    }

    @PostMapping("/handle-login-freelancer")
    public String handleFreelancerLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            ModelMap modelMap,
            HttpSession httpSession
    ){

        Optional<Freelancer> optionalFreelancer =  freelancerServiceImplementation.loginFreelancer(email, password);
        if(optionalFreelancer.isEmpty()){
            modelMap.addAttribute("message", "user does not exit - try again");
            return "redirect:/login-freelancer";
        }
        Freelancer freelancer = optionalFreelancer.get();

        modelMap.addAttribute("freelancer", freelancer);

        httpSession.setAttribute("SessionData", email);
        httpSession.setAttribute("freelancerData", freelancer);

        return "redirect:/freelancerHomepage";

    }

    @GetMapping("/freelancerHomepage")
    public String getFreelancerHompage(HttpSession httpSession, ModelMap modelMap){
        String sessionKey = (String) httpSession.getAttribute("SessionData");
        Freelancer theFreelancer = (Freelancer) httpSession.getAttribute("freelancerData");
        if(sessionKey == null || theFreelancer == null){
            return "loginFreelancer";
        }

        // modelMap.addAttribute("companyName", theCompany.getName());
        modelMap.addAttribute("freelancer", theFreelancer);


        return "freelancerHomepage";
    }

    @GetMapping("/logout-freelancer")
    public String logoutFreelancer(HttpSession httpSession){
        httpSession.invalidate();
        return "redirect:/login-freelancer";
    }

    @GetMapping("/freelancerProfilePage")
    public String getFreelancerProfilePage(HttpSession httpSession, ModelMap modelMap){

        Optional<Freelancer> fo = freelancerRepository.findByEmailAndPassword("frankgye18@gmail.com", "frank"); // we will change this
        if(fo.isPresent()){
            Freelancer freelancer = fo.get();

            // duplicate here
            Path path = storageServiceImplementation.load(freelancer.getProfilepicturename());
            String profileSrc =  MvcUriComponentsBuilder
                    .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                    .build()
                    .toUri()
                    .toString();

            modelMap.addAttribute("freelancer", freelancer);
            modelMap.addAttribute("profilePicturePath", profileSrc);

            return "freelancerProfilepage";
        }

        return null; // will change this
    }

    @PostMapping("/handle-freelancer-profile-update/{id}")
    public String updateFreelancerProfile(
            @PathVariable(name = "id") String freelancerId,
            @ModelAttribute FreelancerDTO freelancerDTO,
            @RequestParam("freelancerPhotoFile") MultipartFile freelancerPhotoFile,
            ModelMap modelMap
    ){
        return null;
    }

}
