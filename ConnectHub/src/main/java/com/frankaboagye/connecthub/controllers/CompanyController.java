package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.CompanyDAO;
import com.frankaboagye.connecthub.dtos.CompanyDTO;
import com.frankaboagye.connecthub.entities.*;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import com.frankaboagye.connecthub.repositories.JobRepository;
import com.frankaboagye.connecthub.repositories.ProjectRepository;
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
import java.util.List;
import java.util.Optional;

import static com.frankaboagye.connecthub.enums.ConnectHubConstant.*;
import static com.frankaboagye.connecthub.enums.ConnectHubProfile.*;

@Controller
@RequiredArgsConstructor
public class CompanyController {

    // note the use of interfaces here
    private final CompanyServiceInterface companyServiceImplementation;
    private final StorageServiceInterface storageServiceImplementation; // it will use the FileSystemStorageService .. since that is what has been configured
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final ProjectRepository projectRepository;

    // company registration
    @GetMapping("/register-company")
    public String registerCompany(ModelMap modelMap){
        return "registerCompany";
    }

    @PostMapping("/handle-register-company")
    public String handleCompanyRegisteration(
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

            Company newCompany = Company.builder()
                    .email(companyDAO.getEmail())
                    .name(companyDAO.getName())
                    .phonenumber(companyDAO.getPhonenumber())
                    .website(companyDAO.getWebsite())
                    .password(companyDAO.getPassword())
                    .profilepicturename(companyPhotoFile.getOriginalFilename())
                    .description(companyDAO.getDescription())
                    .build();

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

            httpSession.setAttribute(CONNECT_HUB_SESSION_DATA.getDescription(), email);
            httpSession.setAttribute(CONNECT_HUB_PROFILE.getDescription(), COMPANY);

            httpSession.setAttribute(CONNECT_HUB_COMPANY_DATA.getDescription(), company);

            return "redirect:/companyHomepage";

        }
        modelMap.addAttribute("errorMessage", "Login Failed, Try Again");
        return "loginCompany";

    }


    @GetMapping("/logout-company")
    public String logoutCompany(HttpSession httpSession){
        httpSession.invalidate();
        return "loginCompany";
    }

    @GetMapping("/companyHomepage")
    public String getCompanyHompage(HttpSession httpSession, ModelMap modelMap){

        String sessionData = (String) httpSession.getAttribute(CONNECT_HUB_SESSION_DATA.getDescription());
        Company companyData = (Company) httpSession.getAttribute(CONNECT_HUB_COMPANY_DATA.getDescription());

        if(companyData != null && sessionData != null){
            List<Job> companyJobs = jobRepository.findAllByCompanyId(companyData.getId());
            List<Project> companyProject = projectRepository.findAllByCompanyId(companyData.getId());
            modelMap.addAttribute("companyJobs", companyJobs);
            modelMap.addAttribute("companyProject", companyProject);
            modelMap.addAttribute("company", companyData);
            modelMap.addAttribute("sessionData", companyData.getEmail());
        } else {
            return "redirect:/login-company";
        }


        return "companyHomepage";
    }


    @GetMapping("/companyProfilepage/{companyId}")
    public String getCompanyProfilePage(@PathVariable("companyId") Long companyId, HttpSession httpSession, ModelMap modelMap) throws IOException {


        Optional<Company> companyOptional = companyRepository.findById(companyId);

        if(companyOptional.isEmpty()){ return "redirect:/loginCompany"; }

        Company theCompany = companyOptional.get();

        Path path = storageServiceImplementation.load(theCompany.getProfilepicturename());
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

    @PostMapping("/handle-company-profile-update/{id}")
    public String updateCompanyProfile(
            @PathVariable(name = "id") Long companyId,
            @ModelAttribute CompanyDTO companyDTO,
            @RequestParam("companyPhotoFile") MultipartFile companyPhotoFile,
            ModelMap modelMap,
            HttpSession httpSession
    ){

        boolean updateFile =  false;

        if(!companyPhotoFile.isEmpty()){updateFile = true;}

        Company company = companyServiceImplementation.updateCompany(companyId, companyDTO, updateFile,companyPhotoFile);
        modelMap.addAttribute("message", "update successful");

        modelMap.addAttribute("company", company);

        httpSession.setAttribute("sessionData",company.getEmail());
        httpSession.setAttribute("companyData", company);

        return "redirect:/companyProfilepage/" + company.getId();

    }




    // will delete later - for dev purpose
    public Company getCisco(){
        Optional<Company> co =  companyRepository.findByEmailAndPassword("cisco@gmail.com", "cisco");
        return co.orElse(null);

    }


}
