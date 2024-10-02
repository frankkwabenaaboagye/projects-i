package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.interfaces.JobServiceInterface;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import com.frankaboagye.connecthub.services.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class JobController {

    private final JobServiceInterface jobServiceImplementation;
    private final CompanyRepository companyRepository;

    @GetMapping("/view-job/{id}")
    public String viewJob(@PathVariable Long id, ModelMap modelMap){

        Job job = jobServiceImplementation.getJobById(id);
        Company company = companyRepository.findById(job.getCompanyId()).orElse(null);

        modelMap.addAttribute("company", company);
        modelMap.addAttribute("job", job);

        return "viewJob";
    }
}
