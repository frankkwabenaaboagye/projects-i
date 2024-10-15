package com.frankaboagye.connecthub.controlllers;

import com.frankaboagye.connecthub.daos.CompanyDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.entities.Project;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.JobServiceInterface;
import com.frankaboagye.connecthub.interfaces.ProjectServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static com.frankaboagye.connecthub.enums.ConnectHubConstant.PROFILE;
import static com.frankaboagye.connecthub.enums.ConnectHubConstant.SESSION_DATA;
import static com.frankaboagye.connecthub.enums.ConnectHubProfile.COMPANY;


@Controller
@RequiredArgsConstructor
public class CompanyController {

    private final StorageServiceInterface storageServiceImplementation; // it will use the FileSystemStorageService .. since that is what has been configured
    private final CompanyRepository companyRepository;
    private final CompanyServiceInterface companyServiceImplementation;
    private final JobServiceInterface jobServiceImplementation;
    private final ProjectServiceInterface projectServiceImplementation;


    @GetMapping("/register-company")
    public String registerCompany(ModelMap modelMap){
        return "registerCompany";
    }

    @PostMapping("/handle-register-company")
    public String handleCompanyRegistration(
            @ModelAttribute CompanyDAO companyDAO,
            @RequestParam("companyPhotoFile") MultipartFile companyPhotoFile,
            ModelMap modelMap
    ){

        // use aspect over here
        // we can do some serious checks
        // logging


        // compare the passwords... although we are doing it with js at the view side

        if(!companyDAO.getPassword().equals(companyDAO.getConfirmPassword())){
            modelMap.addAttribute("message", "registration failed - passwords do not match");
            return "registerCompany";
        }

        try {
            storageServiceImplementation.store(companyPhotoFile);
            Path profilePicturePath = storageServiceImplementation.load(companyPhotoFile.getOriginalFilename()); // do something about this

            Company newCompany = Company.builder()
                    .email(companyDAO.getEmail())
                    .name(companyDAO.getName())
                    .phonenumber(companyDAO.getPhonenumber())
                    .website(companyDAO.getWebsite())
                    .password(companyDAO.getPassword())
                    .profilePictureLocation(profilePicturePath.toString())
                    .description(companyDAO.getDescription())
                    .build();

            // address will be handled at the update

            companyServiceImplementation.registerCompany(newCompany);

            modelMap.addAttribute("message", "registration successful");

            return "loginCompany";
        } catch (Exception e) {
            modelMap.addAttribute("message", "registration failed");
            return "registerCompany";
        }

    }

    @GetMapping("/login-company")
    public String loginCompany(){
        return "loginCompany";
    }

    @PostMapping("/handle-login-company")
    public String handleCompanyLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            ModelMap modelMap,
            HttpSession httpSession,
            RedirectAttributes redirectAttributes
    ){

        Optional<Company> companyData = companyServiceImplementation.loginCompany(email, password);
        if(companyData.isPresent()){
            Company company = companyData.get();

            httpSession.setAttribute(SESSION_DATA.getDescription(), company.getId());  // e.g. ("sessionData", 29919)
            httpSession.setAttribute(PROFILE.getDescription(), COMPANY.getValue());  // e.g. ("company", company)

            return "redirect:/companyHomepage";

        }
        modelMap.addAttribute("message", "Login Failed, Try Again");
        return "loginCompany";

    }


    @GetMapping("/logout-company")
    public String logoutCompany(HttpSession httpSession){
        httpSession.invalidate();
        return "loginCompany";
    }

    @GetMapping("/companyHomepage")
    public String getCompanyHomepage(HttpSession httpSession, ModelMap modelMap){

        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if(sessionData == null){ return "redirect:/login-company"; }

        Company company = companyRepository.findById(sessionData).orElse(null);

        if(company != null){
            List<Job> companyJobs = jobServiceImplementation.getAllJobsByCompanyId(company.getId());
            List<Project> companyProjects = projectServiceImplementation.getAllProjectsByCompanyId(company.getId());

            modelMap.addAttribute("company", company);
            modelMap.addAttribute("companyJobs", companyJobs);
            modelMap.addAttribute("companyProjects", companyProjects);

            httpSession.setAttribute(SESSION_DATA.getDescription(), company.getId());  // e.g. ("sessionData", 29919)
            httpSession.setAttribute(PROFILE.getDescription(), COMPANY.getValue());  // e.g. ("company", company)

        } else {
            modelMap.addAttribute("message" , "login first");
            return "redirect:/login-company";
        }


        return "companyHomepage";
    }


    @GetMapping("/company-profile-page/{companyId}")
    public String getCompanyProfilePage(
            HttpSession httpSession,
            ModelMap modelMap,
            @PathVariable String companyId
    ) throws IOException {


        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if(sessionData == null){ return "redirect:/login-company"; }

        Company company = companyRepository.findById(sessionData).orElse(null);

        if(company == null){ return "redirect:/login-company"; }

        Path path = Paths.get(company.getProfilePictureLocation());

        String profileSrc = MvcUriComponentsBuilder
                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                .build()
                .toUri()
                .toString();

        modelMap.addAttribute("company", theCompany);
        modelMap.addAttribute("profilePicturePath", profileSrc);

        httpSession.setAttribute("sessionData", theCompany.getEmail());
        httpSession.setAttribute("companyData", theCompany);

        return "companyProfilepage";

    }
//
//    @PostMapping("/handle-company-profile-update/{id}")
//    public String updateCompanyProfile(
//            @PathVariable(name = "id") Long companyId,
//            @ModelAttribute CompanyDTO companyDTO,
//            @RequestParam("companyPhotoFile") MultipartFile companyPhotoFile,
//            ModelMap modelMap,
//            HttpSession httpSession
//    ){
//
//        boolean updateFile =  false;
//
//        if(!companyPhotoFile.isEmpty()){updateFile = true;}
//
//        Company company = companyServiceImplementation.updateCompany(companyId, companyDTO, updateFile,companyPhotoFile);
//        modelMap.addAttribute("message", "update successful");
//
//        modelMap.addAttribute("company", company);
//
//        httpSession.setAttribute("sessionData",company.getEmail());
//        httpSession.setAttribute("companyData", company);
//
//        return "redirect:/companyProfilepage/" + company.getId();
//
//    }
//
//
//
//
//    // will delete later - for dev purpose
//    public Company getCisco(){
//        Optional<Company> co =  companyRepository.findByEmailAndPassword("cisco@gmail.com", "cisco");
//        return co.orElse(null);
//
//    }
//
//
}
