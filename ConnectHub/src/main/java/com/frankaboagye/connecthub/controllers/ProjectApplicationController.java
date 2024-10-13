package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.ProjectApplicationDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Freelancer;
import com.frankaboagye.connecthub.entities.Project;
import com.frankaboagye.connecthub.entities.ProjectApplication;
import com.frankaboagye.connecthub.enums.ConnectHubProfile;
import com.frankaboagye.connecthub.interfaces.*;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import com.frankaboagye.connecthub.repositories.FreelancerRepository;
import com.frankaboagye.connecthub.repositories.JobRepository;
import com.frankaboagye.connecthub.repositories.ProjectRepository;
import com.frankaboagye.connecthub.services.ProjectApplicationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.time.LocalDate;

import static com.frankaboagye.connecthub.enums.ApplicationStatus.SUBMITTED;
import static com.frankaboagye.connecthub.enums.ConnectHubConstant.PROFILE;
import static com.frankaboagye.connecthub.enums.ConnectHubConstant.SESSION_DATA;
import static com.frankaboagye.connecthub.enums.ConnectHubProfile.*;

@RequiredArgsConstructor
@Controller
public class ProjectApplicationController {

    private final ProjectApplicationService projectApplicationServiceImplementation;
    private final CompanyRepository companyRepository;
    private final FreelancerRepository freelancerRepository;
    private final CompanyServiceInterface companyServiceImplementation;
    private final StorageServiceInterface storageServiceImplementation;
    private final ProjectRepository projectRepository;

    @PostMapping("/submit-project-application/{projectId}")
    public String handleProjectApplicationSubmission(
            @ModelAttribute ProjectApplicationDAO projectApplicationDAO,
            @PathVariable long projectId,
            HttpSession httpSession,
            @RequestParam MultipartFile resumeFile,
            ModelMap modelMap,
            RedirectAttributes redirectAttributes
    ){

        //TODO

        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            return "redirect:/login-freelancer";
        }

        Company company = project.getCompany();

        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if (sessionData == null) {
            return "redirect:/login-freelancer";
        }

        Freelancer freelancer = freelancerRepository.findById(sessionData).orElse(null);
        if (freelancer == null) {
            return "redirect:/login-freelancer";
        }


        if(resumeFile.isEmpty()){
            return "redirect:/login-freelancer";
        }

        // resumeLocation
        storageServiceImplementation.store(resumeFile);
        Path resumePath = storageServiceImplementation.load(resumeFile.getOriginalFilename());

        ProjectApplication projectApplication = ProjectApplication.builder()
                .resumeLocation(resumePath.toString())
                .applicationDate(LocalDate.now())
                .status(SUBMITTED)
                .freelancer(freelancer)
                .company(company)
                .project(project)
                .build();

        // add freelancer comments - during updates

        projectApplicationServiceImplementation.submitProjectApplication(projectApplication);


        modelMap.addAttribute("freelancer", freelancer);
        modelMap.addAttribute("project", project);
        modelMap.addAttribute("company", company);

        httpSession.setAttribute(PROFILE.getDescription(), FREELANCER.getValue());
        httpSession.setAttribute(SESSION_DATA.getDescription(), freelancer.getId());

        redirectAttributes.addFlashAttribute ("successMessage", "Success! Job Application submitted successfully.");


        return "redirect:/view-and-apply-project/" + project.getId();
    }


}
