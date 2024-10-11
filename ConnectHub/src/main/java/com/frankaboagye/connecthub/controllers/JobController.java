package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.JobDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Freelancer;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.JobServiceInterface;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import com.frankaboagye.connecthub.repositories.FreelancerRepository;
import com.frankaboagye.connecthub.services.CompanyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class JobController {

    private final JobServiceInterface jobServiceImplementation;
    private final CompanyRepository companyRepository;
    private final FreelancerRepository freelancerRepository;
    private final CompanyServiceInterface companyServiceImplementation;


    @GetMapping("/view-job/{id}")
    public String viewJob(@PathVariable Long id, ModelMap modelMap){

        Job job = jobServiceImplementation.getJobById(id);
        Company company = companyRepository.findById(job.getCompanyId()).orElse(null);

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

        return "/jobs/exploreJobsPage";

    }

    @GetMapping("/post-a-job/{companyId}")
    public String postAJob(@PathVariable Long companyId, HttpSession httpSession, ModelMap modelMap){
        Optional<Company> companyOptional = companyRepository.findById(companyId);

        if(companyOptional.isEmpty()){return "redirect:/login-company";}

        Company company = companyOptional.get();
        modelMap.addAttribute("company", company);

        httpSession.setAttribute("companyData", company);
        httpSession.setAttribute("sessionData", company.getEmail());

        return "postJob";
    }

    @PostMapping("/handle-post-a-job")
    public String handleJobPosting(@ModelAttribute JobDAO jobDAO, ModelMap modelMap, HttpSession httpSession){
        // add securuty stuffs later, converstion stuffs

        String sessionData = (String) httpSession.getAttribute("sessionData");
        Company companyData = (Company) httpSession.getAttribute("companyData");

        String stop = "here";

        var date = LocalDate.parse(jobDAO.getDeadline());

        // convert form dao to the object
        Job newJob = Job.builder()
                .companyId(companyData.getId())
                .companyName(companyData.getName())
                .title(jobDAO.getJobTitle())
                .description(jobDAO.getJobDescription())
                .salary(Double.valueOf(jobDAO.getSalary()))
                .skills(jobDAO.getSkills())
                .deadline(date)
                .location(jobDAO.getLocation())
                .build();


        companyServiceImplementation.postAJob(newJob);

        modelMap.addAttribute("company", companyData);

        httpSession.setAttribute("sessionData", sessionData);
        httpSession.setAttribute("companyData", companyData);

        return "redirect:/companyHomepage";
    }

    @GetMapping("view-and-apply-job/{jobId}")
    public String viewAndApplyJob(@PathVariable Long jobId, ModelMap modelMap, HttpSession httpSession){

        return "viewAndApplyJob";
    }

}
