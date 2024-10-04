package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Freelancer;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.interfaces.JobServiceInterface;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import com.frankaboagye.connecthub.repositories.FreelancerRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class JobController {

    private final JobServiceInterface jobServiceImplementation;
    private final CompanyRepository companyRepository;
    private final FreelancerRepository freelancerRepository;


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
}
