package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.JobDAO;
import com.frankaboagye.connecthub.daos.JobUpdateDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Freelancer;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.enums.GeneralSkills;
import com.frankaboagye.connecthub.enums.Item;
import com.frankaboagye.connecthub.enums.TechnologyInterest;
import com.frankaboagye.connecthub.enums.WorkLabel;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.FreelancerServiceInterface;
import com.frankaboagye.connecthub.interfaces.JobServiceInterface;
import com.frankaboagye.connecthub.utils.CompanyUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

import static com.frankaboagye.connecthub.enums.ConnectHubConstant.PROFILE;
import static com.frankaboagye.connecthub.enums.ConnectHubConstant.SESSION_DATA;
import static com.frankaboagye.connecthub.enums.ConnectHubProfile.COMPANY;
import static com.frankaboagye.connecthub.enums.ConnectHubProfile.FREELANCER;
import static com.frankaboagye.connecthub.enums.Item.LABELS;
import static com.frankaboagye.connecthub.enums.Item.TECH_INTEREST;

@RequiredArgsConstructor
@Controller
public class JobController {

    private final CompanyServiceInterface companyServiceImplementation;
    private final JobServiceInterface jobServiceImplementation;
    private final FreelancerServiceInterface freelancerServiceImplementation;


    @GetMapping("/view-job/{jobId}")
    public String viewJob(@PathVariable Long jobId, ModelMap modelMap, HttpSession httpSession) {

        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if (sessionData == null) {
            return "redirect:/login-company";
        }

        Company company = companyServiceImplementation.getCompanyById(sessionData).orElse(null);
        if (company == null) {
            return "redirect:/login-company";
        }

        Job job = jobServiceImplementation.getJobById(jobId);
        if (job == null) {
            return "redirect:/login-company";
        }

        // you can check for mismatch from of the company from the: jobId and the sessionID
        // we have to get a DTO for the company

        modelMap.addAttribute("company", company);
        modelMap.addAttribute("job", job);

        Set<String> defaultTechInterest = CompanyUtils.getDefaultValueFor(TECH_INTEREST);
        Set<String> defaultAssociatedLabels = CompanyUtils.getDefaultValueFor(LABELS);

        modelMap.addAttribute("defaultTechInterest", defaultTechInterest);
        modelMap.addAttribute("defaultAssociatedLabels", defaultAssociatedLabels);

        CompanyUtils.setHttpSession(httpSession, company);

        return "viewJob";
    }


    @GetMapping("/explore-jobs/{freelancerId}")
    public String exploreJobs(
        @PathVariable Long freelancerId,
        ModelMap modelMap,
        HttpSession httpSession
    ){

        // TODO: will handle this well with spring security
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

        modelMap.addAttribute("freelancer", freelancer);

        // explore jobs
        List<Job>  jobs = jobServiceImplementation.getAllJobs();
        modelMap.addAttribute("jobs", jobs);

        httpSession.setAttribute(PROFILE.getDescription(), FREELANCER.getValue());
        httpSession.setAttribute(SESSION_DATA.getDescription(), freelancer.getId()); // session Id will be the id of the freelancer

        return "/jobs/exploreJobsPage";


    }

    @GetMapping("/post-a-job/{companyId}")
    public String postAJob(
            @PathVariable Long companyId,
            HttpSession httpSession,
            ModelMap modelMap
    ) {


        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if (sessionData == null) {
            return "redirect:/login-company";
        }

        Company company = companyServiceImplementation.getCompanyById(sessionData).orElse(null);
        if (company == null) {
            return "redirect:/login-company";
        }


        modelMap.addAttribute("company", company);

        httpSession.setAttribute(SESSION_DATA.getDescription(), company.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(PROFILE.getDescription(), COMPANY.getValue());  // e.g. ("company", company)

        List<String> availableSkills = GeneralSkills.getAvailableSkills();
        List<String> availableLabels = WorkLabel.getAvailableWorkLabels();
        Set<String> availableTechnologies = TechnologyInterest.getAvailableTechnologyInterest();

        modelMap.addAttribute("availableSkills", availableSkills);
        modelMap.addAttribute("availableLabels", availableLabels);
        modelMap.addAttribute("availableTechnologies", availableTechnologies);

        return "postJob";
    }

    @PostMapping("/handle-post-a-job")
    public String handleJobPosting(
            @ModelAttribute JobDAO jobDAO,
            ModelMap modelMap,
            HttpSession httpSession
    ) {
        // add securuty stuffs later, converstion stuffs

        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if (sessionData == null) {
            return "redirect:/login-company";
        }

        Company company = companyServiceImplementation.getCompanyById(sessionData).orElse(null);
        if (company == null) {
            return "redirect:/login-company";
        }

        List<String> skillForJob = new ArrayList<>(jobDAO.getSkills());
        skillForJob.removeIf(String::isEmpty);

        Optional.ofNullable(jobDAO.getOtherSkills())
                .ifPresent( otherSkills -> skillForJob.addAll(
                        otherSkills.stream().filter( s -> !s.isEmpty()).toList()
                ));// Add other skills to the existing list


        Job newJob = Job.builder()
                .company(company)
                .title(jobDAO.getTitle())
                .description(jobDAO.getDescription())
                .salary(Double.valueOf(jobDAO.getSalary()))
                .skills(new HashSet<>(skillForJob))
                .deadline(LocalDate.parse(jobDAO.getDeadline()))
                .location(jobDAO.getLocation())
                .moreInformation(jobDAO.getMoreInformation())
                .build();

        // associated labels, responsibilities, technology interest will be added - when company is updating the job;

        companyServiceImplementation.postAJob(newJob);

        modelMap.addAttribute("company", company);

        httpSession.setAttribute(SESSION_DATA.getDescription(), company.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(PROFILE.getDescription(), COMPANY.getValue());  // e.g. ("company", company)


        return "redirect:/company-homepage";
    }


    @GetMapping("/view-and-apply-job/{jobId}")
    public String viewAndApplyJob(
            @PathVariable Long jobId,
            ModelMap modelMap,
            HttpSession httpSession
    ){

        // TODO: will handle this well with spring security
        // TODO: handle duplicates
        // TODO: handle checks well
        // TODO: use aop

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

        String successMessage = (String) modelMap.get("successMessage");

        modelMap.addAttribute("freelancer", freelancer);

        Job job = jobServiceImplementation.getJobById(jobId);
        Company company = job.getCompany();


        if(company == null){return "redirect:/login-freelancer";}

        modelMap.addAttribute("company", company);
        modelMap.addAttribute("job", job);

        List<Job> companyJobs = jobServiceImplementation.getAllJobsByCompanyId(company.getId());
        modelMap.addAttribute("companyJobs", companyJobs);

        // the company profile src
        Path profilePath = Paths.get(company.getProfilePictureLocation());
        String profileSrc = MvcUriComponentsBuilder
                .fromMethodName(FileUploadController.class, "serveFile", profilePath.getFileName().toString())
                .build()
                .toUri()
                .toString();

        modelMap.addAttribute("profilePicturePath", profileSrc);

        modelMap.addAttribute("successMessage", successMessage);

        httpSession.setAttribute(SESSION_DATA.getDescription(), freelancer.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(PROFILE.getDescription(), FREELANCER.getValue());  // e.g. ("freelancer", freelancer)


        return "viewAndApplyJob";


    }

    @PostMapping("/handle-company-job-update/{jobId}")
    public String updateCompanyJob(
            @ModelAttribute JobUpdateDAO jobUpdateDAO,
            @PathVariable Long jobId,
            ModelMap modelMap,
            HttpSession httpSession
    ) {



        // CompanyUtils.setHttpSession(httpSession, com); // TODO: set the session

        return null;

    }


    @PostMapping("/update-job-items/{jobId}/{item}")
    public String updateJobItems(@PathVariable Long jobId, @PathVariable Item item, Set<String> theItems, ModelMap modelMap, HttpSession httpSession){
        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if (sessionData == null) {
            return "redirect:/login-company";
        }

        Company company = companyServiceImplementation.getCompanyById(sessionData).orElse(null);
        if (company == null) {
            return "redirect:/login-company";
        }

        Job job = jobServiceImplementation.getJobById(jobId);
        if(job == null){ return "redirect:/login-company";}

        if(item.equals(TECH_INTEREST)){
            CompanyUtils.updateTechInterest(job, theItems);
        }


        CompanyUtils.setHttpSession(httpSession, company);

        return "redirect:/view-job/" + jobId;
    }

    @GetMapping("/default-items/{section}")
    @ResponseBody
    public Set<String> getDefaultItems(@PathVariable Item section) {
        return CompanyUtils.getDefaultValueFor(section);
    }

}
