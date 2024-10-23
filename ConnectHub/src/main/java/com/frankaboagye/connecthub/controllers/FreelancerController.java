package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.FreelancerDAO;
import com.frankaboagye.connecthub.daos.FreelancerUpdateDAO;
import com.frankaboagye.connecthub.entities.Freelancer;
import com.frankaboagye.connecthub.entities.Resume;
import com.frankaboagye.connecthub.enums.Gender;
import com.frankaboagye.connecthub.interfaces.FreelancerServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import com.frankaboagye.connecthub.repositories.ResumeRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.frankaboagye.connecthub.enums.ConnectHubConstant.PROFILE;
import static com.frankaboagye.connecthub.enums.ConnectHubConstant.SESSION_DATA;
import static com.frankaboagye.connecthub.enums.ConnectHubProfile.FREELANCER;

@Controller
@RequiredArgsConstructor
public class FreelancerController {

    private final StorageServiceInterface storageServiceImplementation;
    private final FreelancerServiceInterface freelancerServiceImplementation;
    private final ResumeRepository resumeRepository;

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

        List<Resume> resumes = freelancer.getResumes();
        modelMap.addAttribute("resumes", resumes);

        if (!resumes.isEmpty()) {
            Resume resume = resumes.getFirst();
            // only these two
            modelMap.addAttribute("resumeSrc", getDocumentSrc(Paths.get(resume.getLocation())));
            modelMap.addAttribute("resumeTitle", resume.getTitle());
        }


        httpSession.setAttribute(SESSION_DATA.getDescription(), freelancer.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(PROFILE.getDescription(), FREELANCER.getValue());  // e.g. ("freelancer", freelancer)

        return "freelancerProfilepage";

    }


    @PostMapping("/handle-freelancer-profile-update/{freelancerId}")
    public String updateFreelancerProfile(
            @PathVariable Long freelancerId,
            MultipartFile freelancerPhotoFile,
            MultipartFile newResume,
            HttpSession httpSession,
            @ModelAttribute FreelancerUpdateDAO freelancerUpdateDAO,
            ModelMap modelMap
    ){

        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription()); // todo: handle duplicate
        if(sessionData == null){
            modelMap.addAttribute("message", "session data does not exist");
            return "redirect:/login-freelancer";
        }

        Freelancer freelancer = freelancerServiceImplementation.getFreelancerById(sessionData).orElse(null);
        if(freelancer == null){
            modelMap.addAttribute("message", "Freelancer not found");
            return "redirect:/login-freelancer";
        }

        // update // TODO: handle this properly
        freelancer.setGender(Gender.valueOf(freelancerUpdateDAO.getGender().toUpperCase()));
        freelancer.setFullName(freelancerUpdateDAO.getFullName());
        freelancer.setDateOfBirth(freelancerUpdateDAO.getDateOfBirth());
        freelancer.setPhoneNumber(freelancerUpdateDAO.getPhoneNumber());
        freelancer.setEducation(freelancerUpdateDAO.getEducation());
        freelancer.setBasicCharge(freelancerUpdateDAO.getBasicCharge());
        freelancer.setLinkedin(freelancerUpdateDAO.getLinkedin());

        List<String> theSkills = new ArrayList<>(freelancerUpdateDAO.getSkills());
        theSkills.removeIf(String::isEmpty);

        freelancer.setSkills(new HashSet<>(theSkills));


        if(!freelancerPhotoFile.isEmpty()){
            storageServiceImplementation.store(freelancerPhotoFile);
            Path path = storageServiceImplementation.load(freelancerPhotoFile.getOriginalFilename());
            freelancer.setProfilepictureurl(path.toString());
        }

        if(!newResume.isEmpty()){
            storageServiceImplementation.store(newResume);
            Path path = storageServiceImplementation.load(newResume.getOriginalFilename());
            Resume resume = Resume.builder()
                    .fileName(newResume.getOriginalFilename())
                    .location(path.toString())
                    .uploadDate(LocalDate.now())
                    .title(newResume.getOriginalFilename()) // todo: will change the logic for title
                    .description("default for now")
                    .freelancer(freelancer)
                    .isPrimary(true) // default for now
                    .build();

            freelancer.getResumes().add(resume);

            Freelancer updatedFreelancer = freelancerServiceImplementation.updateFreelancer(freelancer);

            resume.setFreelancer(updatedFreelancer);

            resumeRepository.save(resume);


        } else{
            freelancerServiceImplementation.updateFreelancer(freelancer);

        }

        // job applications
        // project applications


        httpSession.setAttribute(SESSION_DATA.getDescription(), freelancer.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(PROFILE.getDescription(), FREELANCER.getValue());  // e.g. ("freelancer", freelancer)


        return "redirect:/freelancer-profilepage";
    }

    public String getDocumentSrc(Path path) {

        return MvcUriComponentsBuilder
                .fromMethodName(FileUploadController.class, "displayFile", path.getFileName().toString())
                .build()
                .toUri()
                .toString();
    }

}
