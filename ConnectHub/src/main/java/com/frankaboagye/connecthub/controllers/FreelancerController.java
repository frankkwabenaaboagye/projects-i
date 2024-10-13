//package com.frankaboagye.connecthub.controllers;
//
//import com.frankaboagye.connecthub.daos.FreelancerDAO;
//import com.frankaboagye.connecthub.dtos.CompanyDTO;
//import com.frankaboagye.connecthub.dtos.FreelancerDTO;
//import com.frankaboagye.connecthub.entities.Company;
//import com.frankaboagye.connecthub.entities.Freelancer;
//import com.frankaboagye.connecthub.enums.Gender;
//import com.frankaboagye.connecthub.interfaces.FreelancerServiceInterface;
//import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
//import com.frankaboagye.connecthub.repositories.FreelancerRepository;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.io.Resource;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.net.URL;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalDate;
//import java.util.Optional;
//
//import static com.frankaboagye.connecthub.enums.ConnectHubConstant.PROFILE;
//import static com.frankaboagye.connecthub.enums.ConnectHubConstant.SESSION_DATA;
//import static com.frankaboagye.connecthub.enums.ConnectHubProfile.COMPANY;
//import static com.frankaboagye.connecthub.enums.ConnectHubProfile.FREELANCER;
//
//@Controller
//@RequiredArgsConstructor
//public class FreelancerController {
//
//    private final FreelancerServiceInterface freelancerServiceImplementation;
//    private final StorageServiceInterface storageServiceImplementation;
//    private final FreelancerRepository freelancerRepository;
//
//    @GetMapping("/register-freelancer")
//    public String registerFreelancer(){
//        return "registerFreelancer";
//    }
//
//    @PostMapping("/handle-register-freelancer")
//    public String handleFreelancerRegistration(
//            @ModelAttribute FreelancerDAO freelancerDAO,
//            @RequestParam("freelancerPhotoFile") MultipartFile freelancerPhotoFile,
//            ModelMap modelMap,
//            HttpSession httpSession,
//            RedirectAttributes redirectAttributes
//    ){
//
//        // will make the password check better
//        if(!freelancerDAO.getPassword().equals(freelancerDAO.getConfirmPassword())){
//            modelMap.addAttribute("message", "Passwords do not match");
//            return "redirect:/register-freelancer";
//        }
//
//        // do the conversion in the helper class
//
//        Freelancer newFreelancer = Freelancer.builder()
//                .email(freelancerDAO.getEmail())
//                .gender(Gender.valueOf(freelancerDAO.getGender().toUpperCase()))
//                .fullName(freelancerDAO.getFullName())
//                .dateOfBirth(LocalDate.parse(freelancerDAO.getDateOfBirth()))
//                .linkedin(freelancerDAO.getLinkedin())
//                .phoneNumber(freelancerDAO.getPhoneNumber())
//                .education(freelancerDAO.getEducation())
//                .basicCharge(Double.parseDouble(freelancerDAO.getBasicCharge()))
//                .password(freelancerDAO.getPassword())
//                .skills(freelancerDAO.getSkills())
//                .build();
//
//        // profile picture url
//
//        storageServiceImplementation.store(freelancerPhotoFile);
//        Path photoFilePath = storageServiceImplementation.load(freelancerPhotoFile.getOriginalFilename());
//
//        newFreelancer.setProfilepictureurl(photoFilePath.toString());
//
//        freelancerServiceImplementation.registerFreelanceer(newFreelancer);
//
//        redirectAttributes.addFlashAttribute("message", "Freelancer registered successfully");
//
//        return "redirect:/login-freelancer";
//
//    }
//
//
//    @GetMapping("/login-freelancer")
//    public String freelancerLogin(ModelMap modelMap){
//
//        if(modelMap.containsAttribute("message")){
//            modelMap.addAttribute("message", "Freelancer registered successfully!!");
//        }
//
//        return "loginFreelancer";
//    }
//
//    @PostMapping("/handle-login-freelancer")
//    public String handleFreelancerLogin(
//            @RequestParam("email") String email,
//            @RequestParam("password") String password,
//            ModelMap modelMap,
//            HttpSession httpSession
//    ){
//
//        Optional<Freelancer> optionalFreelancer =  freelancerServiceImplementation.loginFreelancer(email, password);
//        if(optionalFreelancer.isEmpty()){
//            modelMap.addAttribute("message", "user does not exit - try again");
//            return "redirect:/login-freelancer";
//        }
//        Freelancer freelancer = optionalFreelancer.get();
//
//        modelMap.addAttribute("freelancer", freelancer);
//
//        httpSession.setAttribute("SessionData", email);
//        httpSession.setAttribute("freelancerData", freelancer);
//
//        httpSession.setAttribute(SESSION_DATA.getDescription(), freelancer.getId());  // e.g. ("sessionData", 29919)  // for the freelancer
//        httpSession.setAttribute(PROFILE.getDescription(), COMPANY.getValue());  // e.g. ("company", company)
//
//
//        return "redirect:/freelancerHomepage";
//
//    }
//
//    @GetMapping("/freelancerHomepage")
//    public String getFreelancerHompage(HttpSession httpSession, ModelMap modelMap){
//
//        String sessionData = (String) httpSession.getAttribute(SESSION_DATA.getDescription());
//        if(sessionData == null){
//            modelMap.addAttribute("message", "session data does not exist");
//            return "redirect:/login-freelancer";
//        }
//
//        Freelancer freelancer = freelancerRepository.findById(Long.parseLong(sessionData)).orElse(null);
//        if(freelancer == null){
//            modelMap.addAttribute("message", "user does not exit - try again");
//            return "loginFreelancer";
//        }
//
//        modelMap.addAttribute("freelancer", freelancer);
//
//        httpSession.setAttribute(PROFILE.getDescription(), FREELANCER.getValue());
//        httpSession.setAttribute(SESSION_DATA.getDescription(), freelancer.getId());
//
//
//        return "freelancerHomepage";
//    }
//
//    @GetMapping("/logout-freelancer")
//    public String logoutFreelancer(HttpSession httpSession){
//        httpSession.invalidate();
//        return "redirect:/login-freelancer";
//    }
//
//    @GetMapping("/freelancerProfilePage")
//    public String getFreelancerProfilePage(HttpSession httpSession, ModelMap modelMap){
//
//        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription()); // the id of the freelancer
//
//        Freelancer freelancer = freelancerRepository.findById(sessionData).orElse(null);
//
//        if(freelancer == null){
//            return "redirect:/login-freelancer";
//        }
//
//
//        Path path = Paths.get(freelancer.getProfilepictureurl());
//        String profileSrc = MvcUriComponentsBuilder
//                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
//                .build()
//                .toUri()
//                .toString();
//
//        modelMap.addAttribute("freelancer", freelancer);
//        modelMap.addAttribute("profilePicturePath", profileSrc);
//
//        return "freelancerProfilepage";
//
//    }
//
//    @PostMapping("/handle-freelancer-profile-update/{id}")
//    public String updateFreelancerProfile(
//            @PathVariable(name = "id") String freelancerId,
//            @ModelAttribute FreelancerDTO freelancerDTO,
//            @RequestParam("freelancerPhotoFile") MultipartFile freelancerPhotoFile,
//            ModelMap modelMap
//    ){
//        return null;
//    }
//
//}
