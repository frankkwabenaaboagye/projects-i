package com.frankaboagye.connecthub.controlllers;

import com.frankaboagye.connecthub.daos.FreelancerDAO;
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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

import static com.frankaboagye.connecthub.enums.ConnectHubConstant.PROFILE;
import static com.frankaboagye.connecthub.enums.ConnectHubConstant.SESSION_DATA;
import static com.frankaboagye.connecthub.enums.ConnectHubProfile.FREELANCER;

@Controller
@RequiredArgsConstructor
public class FreelancerController {

    private final StorageServiceInterface storageServiceImplementation;
    private final FreelancerServiceInterface freelancerServiceImplementation;

    @GetMapping("/register-freelancer")
    public String registerFreelancer() {
        return "registerFreelancer";

    }

    @PostMapping("/handle-register-freelancer")
    public String handleFreelancerRegistration(
            @ModelAttribute FreelancerDAO freelancerDAO,
            @RequestParam("freelancerPhotoFile") MultipartFile freelancerPhotoFile,
            ModelMap modelMap,
            HttpSession httpSession,
            RedirectAttributes redirectAttributes
    ){
        // will make the password check better
        if(!freelancerDAO.getPassword().equals(freelancerDAO.getConfirmPassword())){
            modelMap.addAttribute("message", "Passwords do not match");
            return "redirect:/register-freelancer";
        }

        Freelancer newFreelancer = Freelancer.builder()
                .email(freelancerDAO.getEmail())
                .gender(Gender.valueOf(freelancerDAO.getGender().toUpperCase()))
                .fullName(freelancerDAO.getFullName())
                .dateOfBirth(LocalDate.parse(freelancerDAO.getDateOfBirth()))
                .linkedin(freelancerDAO.getLinkedin())
                .phoneNumber(freelancerDAO.getPhoneNumber())
                .education(freelancerDAO.getEducation())
                .basicCharge(Double.parseDouble(freelancerDAO.getBasicCharge()))
                .password(freelancerDAO.getPassword()) // TODO: will use spring security to handle this later
                .skills(freelancerDAO.getSkills())
                .build();

        // profile picture
        storageServiceImplementation.store(freelancerPhotoFile);
        Path photoFilePath  = storageServiceImplementation.load(freelancerPhotoFile.getOriginalFilename());

        newFreelancer.setProfilepictureurl(photoFilePath.toString()); // TODO: will handle this well later

        freelancerServiceImplementation.registerFreelancer(newFreelancer);

        redirectAttributes.addFlashAttribute("message", "Freelancer registered successfully");

        return "redirect:/login-freelancer";

    }

    @GetMapping("/login-freelancer") // TODO: will handle login well with spring security later
    public String loginFreelancer(ModelMap modelMap) {
        return "loginFreelancer";
    }

    @PostMapping("/handle-login-freelancer")
    public String handleFreelancerLogin(
            @RequestParam String email,
            @RequestParam String password,
            ModelMap modelMap,
            HttpSession httpSession
    ){

        // TODO: will implement logics well later - just want to establish a simple logic here

        Optional<Freelancer> optionalFreelancer = freelancerServiceImplementation.loginFreelancer(email, password);
        if(optionalFreelancer.isEmpty()){
            modelMap.addAttribute("message", "Freelancer not found");
            return "redirect:/login-freelancer";
        }

        Freelancer freelancer = optionalFreelancer.get();

        modelMap.addAttribute("freelancer", freelancer);

        httpSession.setAttribute(SESSION_DATA.getDescription(), freelancer.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(PROFILE.getDescription(), FREELANCER.getValue());  // e.g. ("freelancer", freelancer)

        return "redirect:/freelancerHomepage";

    }

    @GetMapping("freelancerHomepage")
    public String getFreelancerHomepage(
            HttpSession httpSession,
            ModelMap modelMap
    ){

        // TODO: will handle the logic well - at a later time

        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if(sessionData == null){
            modelMap.addAttribute("message", "session data does not exist");
            return "redirect:/login-freelancer";
        }

        Freelancer freelancer = freelancerServiceImplementation.getFreelancerById(sessionData).orElse(null);
        if(freelancer == null){
            modelMap.addAttribute("message", "Freelancer not found");
            return "redirect:/login-freelancer";
        }

        modelMap.addAttribute("freelancer", freelancer);

        // TODO: fix code duplication

        httpSession.setAttribute(SESSION_DATA.getDescription(), freelancer.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(PROFILE.getDescription(), FREELANCER.getValue());  // e.g. ("freelancer", freelancer)

        return "freelancerHomepage";

    }

    @GetMapping("logout-freelancer")
    public String logoutFreelancer(HttpSession httpSession){
        // TODO: will use spring security later
        httpSession.invalidate();
        return "redirect:/login-freelancer";
    }

    @GetMapping("/freelancer-profilepage")
    public String getFreelancerProfilePage(
            HttpSession httpSession,
            ModelMap modelMap

    ){

        // TODO: handle duplicates
        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if(sessionData == null){
            modelMap.addAttribute("message", "session data does not exist");
            return "redirect:/login-freelancer";
        }

        Freelancer freelancer = freelancerServiceImplementation.getFreelancerById(sessionData).orElse(null);
        if(freelancer == null){
            modelMap.addAttribute("message", "Freelancer not found");
            return "redirect:/login-freelancer";
        }



        Path path = Paths.get(freelancer.getProfilepictureurl());
        String profileSrc = MvcUriComponentsBuilder
                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                .build()
                .toUri()
                .toString();

        modelMap.addAttribute("freelancer", freelancer);
        modelMap.addAttribute("profilePicturePath", profileSrc);

        return "freelancerProfilepage";

    }


    public String updateFreelancerProfile(

    ){
        // TODO: update logic for freelancer profile
        return null;
    }

}
