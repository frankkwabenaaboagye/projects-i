package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.CompanyDAO;
import com.frankaboagye.connecthub.daos.JobDAO;
import com.frankaboagye.connecthub.daos.ProjectDAO;
import com.frankaboagye.connecthub.dtos.CompanyDTO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.entities.Project;
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

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/login-company")
    public String loginCompany(){
        return "loginCompany";
    }


    @GetMapping("/logout-company")
    public String logoutCompany(HttpSession httpSession){
        httpSession.invalidate();
        return "loginCompany";
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

        if(!companyDAO.getCompanyPassword().equals(companyDAO.getCompanyConfirmPassword())){
            modelMap.addAttribute("message", "registration failed - passwords do not match");
            return "registerCompany";
        }

        try {
            storageServiceImplementation.store(companyPhotoFile);

            Company company = Company.builder()
                    .name(companyDAO.getCompanyName())
                    .email(companyDAO.getCompanyEmail())
                    .phonenumber(companyDAO.getCompanyPhoneNumber())
                    .password(companyDAO.getCompanyPassword())
                    .website(companyDAO.getCompanyWebsite())
                    .profilepicturename(companyPhotoFile.getOriginalFilename())
                    .build();

            companyServiceImplementation.registerCompany(company);

            modelMap.addAttribute("message", "registration successful");

            return "loginCompany";
        } catch (Exception e) {
            modelMap.addAttribute("message", "registration failed");
            return "registerCompany";
        }

    }

    @PostMapping("/handle-login-company")
    public String handleCompanyLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            ModelMap modelMap,
            HttpSession httpSession
    ){

        Optional<Company> companyData = companyServiceImplementation.loginCompany(email, password);
        if(companyData.isPresent()){
            Company company = companyData.get();

            httpSession.setAttribute("sessionData",email);
            httpSession.setAttribute("companyData", company);
            return "redirect:/companyHomepage";

        }
        modelMap.addAttribute("errorMessage", "Login Failed, Try Again");
        return "loginCompany";

    }


    @GetMapping("/companyHomepage")
    public String getCompanyHompage(HttpSession httpSession, ModelMap modelMap){

        String sessionData = (String) httpSession.getAttribute("sessionData");
        Company companyData = (Company) httpSession.getAttribute("companyData");

        if(companyData != null && sessionData == null){
            List<Job> companyJobs = jobRepository.findAllByCompanyId(companyData.getId());
            List<Project> companyProject = projectRepository.findAllByCompanyId(companyData.getId());
            modelMap.addAttribute("companyJobs", companyJobs);
            modelMap.addAttribute("companyProject", companyProject);
        } else {
            return "redirect:/login-company";

        }

        modelMap.addAttribute("company", companyData);
        modelMap.addAttribute("sessionData", sessionData);

        return "companyHomepage";
    }


    @GetMapping("/companyProfilepage")
    public String getCompanyProfilePage(HttpSession httpSession, ModelMap modelMap) throws IOException {


        Company theCompany = getCisco();

        Path path = storageServiceImplementation.load(theCompany.getProfilepicturename());
        String profileSrc = MvcUriComponentsBuilder
                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                .build()
                .toUri()
                .toString();

        modelMap.addAttribute("company", theCompany);
        modelMap.addAttribute("profilePicturePath", profileSrc);

        return "companyProfilepage";


        // Path companyPicturePath = storageServiceImplementation.load(theCompany.getProfilepicturename());

    }

    @PostMapping("/handle-company-profile-update/{id}")
    public String updateCompanyProfile(
            @PathVariable(name = "id") String companyId,
            @ModelAttribute CompanyDTO companyDTO,
            @RequestParam("companyPhotoFile") MultipartFile companyPhotoFile,
            ModelMap modelMap
    ){

        Long id = Long.parseLong(companyId);

        boolean updateFile =  false;

        if(!companyPhotoFile.isEmpty()){updateFile = true;}

        Company company = companyServiceImplementation.updateCompany(id, companyDTO, updateFile,companyPhotoFile);
        modelMap.addAttribute("message", "update successful");

        modelMap.addAttribute("company", company);

        return "redirect:/companyProfilepage";

    }

    @GetMapping("/post-a-job")
    public String postAJob(){
        return "postJob";
    }

    @PostMapping("/handle-post-a-job")
    public String handleJobPosting(@ModelAttribute JobDAO jobDAO, ModelMap modelMap, HttpSession httpSession){
        // add securuty stuffs later, converstion stuffs

        String stop = "here";

        var date = LocalDate.parse(jobDAO.getDeadline());

        // use cisco id for now

        // convert form dao to the object
        Job newJob = Job.builder()
                .companyId(getCisco().getId())
                .title(jobDAO.getJobTitle())
                .description(jobDAO.getJobDescription())
                .salary(Double.valueOf(jobDAO.getSalary()))
                .skills(jobDAO.getSkills())
                .deadline(date)
                .location(jobDAO.getLocation())
                .build();


        companyServiceImplementation.postAJob(newJob);

        modelMap.addAttribute("company", getCisco());
        modelMap.addAttribute("SessionData", getCisco().getEmail());

        return "redirect:/companyHomepage";
    }




    // will delete later - for dev purpose
    public Company getCisco(){
        Optional<Company> co =  companyRepository.findByEmailAndPassword("cisco@gmail.com", "cisco");
        return co.orElse(null);

    }


}
