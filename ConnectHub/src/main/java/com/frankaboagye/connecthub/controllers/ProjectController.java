package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.entities.Project;
import com.frankaboagye.connecthub.interfaces.JobServiceInterface;
import com.frankaboagye.connecthub.interfaces.ProjectServiceInterface;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class ProjectController {

    private final ProjectServiceInterface projectServiceImplementation;
    private final CompanyRepository companyRepository;

    @GetMapping("/view-project/{id}")
    public String viewJob(@PathVariable Long id, ModelMap modelMap){

        Project project = projectServiceImplementation.getProjectById(id);
        Company company = companyRepository.findById(project.getCompanyId()).orElse(null);

        modelMap.addAttribute("company", company);
        modelMap.addAttribute("project", project);

        return "viewProject";
    }
}
