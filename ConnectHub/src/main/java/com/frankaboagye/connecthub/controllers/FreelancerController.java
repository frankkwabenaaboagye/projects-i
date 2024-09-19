package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.FreelancerDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Freelancer;
import com.frankaboagye.connecthub.enums.Gender;
import com.frankaboagye.connecthub.interfaces.FreelancerServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class FreelancerController {

    private final FreelancerServiceInterface freelancerServiceImplementation;
    private final StorageServiceInterface storageServiceImplementation;

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
        return "loginFreelancer";

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
            return "loginFreelancer";
        }
        Freelancer freelancer = optionalFreelancer.get();

        modelMap.addAttribute("freelancer", freelancer);

        httpSession.setAttribute("SessionData", email);
        httpSession.setAttribute("freelancerData", freelancer);

        return "freelancerHomepage";

    }

    @GetMapping("/freelancerHomepage")
    public String getCompanyHompage(HttpSession httpSession, ModelMap modelMap){
        String sessionKey = (String) httpSession.getAttribute("SessionData");
        if(sessionKey == null){
            return "loginCompany";
        }

        Company theCompany = (Company) httpSession.getAttribute("companyData");

        // modelMap.addAttribute("companyName", theCompany.getName());
        modelMap.addAttribute("company", theCompany);


        return "companyHomepage";
    }

}
