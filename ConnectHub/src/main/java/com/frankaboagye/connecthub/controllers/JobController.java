package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.JobDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Freelancer;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.enums.GeneralSkills;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.JobServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import com.frankaboagye.connecthub.repositories.FreelancerRepository;
import com.frankaboagye.connecthub.repositories.JobRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.frankaboagye.connecthub.enums.ConnectHubConstant.CONNECT_HUB_PROFILE;
import static com.frankaboagye.connecthub.enums.ConnectHubConstant.CONNECT_HUB_SESSION_DATA;
import static com.frankaboagye.connecthub.enums.ConnectHubProfile.COMPANY;

@RequiredArgsConstructor
@Controller
public class JobController {

    private final JobServiceInterface jobServiceImplementation;
    private final CompanyRepository companyRepository;
    private final FreelancerRepository freelancerRepository;
    private final CompanyServiceInterface companyServiceImplementation;
    private final StorageServiceInterface storageServiceImplementation;
    private final JobRepository jobRepository;


    @GetMapping("/view-job/{id}")
    public String viewJob(@PathVariable Long id, ModelMap modelMap, HttpSession httpSession){

        Job job = jobServiceImplementation.getJobById(id);
        Company company = job.getCompany();

        modelMap.addAttribute("company", company);
        modelMap.addAttribute("job", job);

        return "viewJob";
    }

    @GetMapping("/explore-jobs/{freelancerId}")
    public String exploreJobs(@PathVariable Long freelancerId, ModelMap modelMap, HttpSession httpSession) {

        // for dev-purpose - will change this
        Freelancer freelancer = freelancerRepository.findById(freelancerId).orElse(null);
        modelMap.addAttribute("freelancer", freelancer);

        List<Job> jobs  = jobServiceImplementation.getAllJobs();
        modelMap.addAttribute("jobs", jobs);

        httpSession.setAttribute("freelancer", freelancer);

        return "/jobs/exploreJobsPage";

    }

    @GetMapping("/post-a-job/{companyId}")
    public String postAJob(@PathVariable Long companyId, HttpSession httpSession, ModelMap modelMap){
        Optional<Company> companyOptional = companyRepository.findById(companyId);

        if(companyOptional.isEmpty()){return "redirect:/login-company";}

        Company company = companyOptional.get();
        modelMap.addAttribute(COMPANY.getValue(), company);

        httpSession.setAttribute(CONNECT_HUB_SESSION_DATA.getDescription(), company.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(CONNECT_HUB_PROFILE.getDescription(), COMPANY.getValue());  // e.g. ("company", company)

        List<String> availableSkills = GeneralSkills.getAvailableSkills();

        modelMap.addAttribute("availableSkills", availableSkills );

        return "postJob";
    }

    @PostMapping("/handle-post-a-job")
    public String handleJobPosting(
            @ModelAttribute JobDAO jobDAO,
            ModelMap modelMap,
            HttpSession httpSession
    ){
        // add securuty stuffs later, converstion stuffs

        String sessionData = (String) httpSession.getAttribute(CONNECT_HUB_SESSION_DATA.getDescription()); // company Id

        Company company = companyRepository.findById(Long.parseLong(sessionData)).orElse(null);
        if(company == null){ return "redirect:/login-company"; }

        List<String> skillForJob = new ArrayList<>(jobDAO.getSkills());
        skillForJob.addAll(jobDAO.getOtherSkills()); // Add other skills to the existing list

        Job newJob = Job.builder()
                .company(company)
                .title(jobDAO.getTitle())
                .description(jobDAO.getDescription())
                .salary(Double.valueOf(jobDAO.getSalary()))
                .skills(skillForJob)
                .deadline(LocalDate.parse(jobDAO.getDeadline()))
                .location(jobDAO.getLocation())
                .moreInformation(jobDAO.getMoreInformation())
                .build();

        // associated labels, responsibilities, technology interest will be added - when company is updating the job;

        companyServiceImplementation.postAJob(newJob);

        modelMap.addAttribute(COMPANY.getValue(), company);

        httpSession.setAttribute(CONNECT_HUB_SESSION_DATA.getDescription(), company.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(CONNECT_HUB_PROFILE.getDescription(), COMPANY.getValue());  // e.g. ("company", company)


        return "redirect:/companyHomepage";
    }

    @GetMapping("view-and-apply-job/{jobId}")
    public String viewAndApplyJob(@PathVariable Long jobId, ModelMap modelMap, HttpSession httpSession){

        Freelancer freelancer = (Freelancer) httpSession.getAttribute("freelancer");
        if(freelancer == null){return "redirect:/login-freelancer";}

        String successMessage = (String) modelMap.get("successMessage");
        modelMap.addAttribute("successMessage", successMessage);

        modelMap.addAttribute("freelancer", freelancer);

        Job job = jobServiceImplementation.getJobById(jobId);
        Company company = job.getCompany();

        if(company == null){return "redirect:/login-company";}

        modelMap.addAttribute("company", company);
        modelMap.addAttribute("job", job);

        // company specific jobs
        List<Job> companyJobs = jobServiceImplementation.getAllJobsByCompanyId(company.getId());
        modelMap.addAttribute("companyJobs", companyJobs);

        Path path = storageServiceImplementation.load(company.getProfilepicturename());
        String profileSrc = MvcUriComponentsBuilder
                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                .build()
                .toUri()
                .toString();
        modelMap.addAttribute("profilePicturePath", profileSrc);


        return "viewAndApplyJob";
    }

}
