package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.ProjectDAO;
import com.frankaboagye.connecthub.dtos.CompanyDTO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.entities.Project;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.JobServiceInterface;
import com.frankaboagye.connecthub.interfaces.ProjectServiceInterface;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class ProjectController {

    private final ProjectServiceInterface projectServiceImplementation;
    private final CompanyServiceInterface companyServiceImplementation;
    private final CompanyRepository companyRepository;

    @GetMapping("/view-project/{id}")
    public String viewJob(@PathVariable Long id, ModelMap modelMap){

        Project project = projectServiceImplementation.getProjectById(id);
        Company company = companyRepository.findById(project.getCompanyId()).orElse(null);

        modelMap.addAttribute("company", company);
        modelMap.addAttribute("project", project);

        return "viewProject";
    }


    @GetMapping("/post-a-project")
    public String postAProject(){
        return "postProject";
    }

    @PostMapping("/handle-post-a-project")
    public String handleProjectPosting(@ModelAttribute ProjectDAO projectDAO, ModelMap modelMap, HttpSession httpSession, @RequestParam("documentFile") MultipartFile documentFile) {
        // add securuty stuffs later, converstion stuffs

        String stop = "here";

        var date = LocalDate.parse(projectDAO.getDeadline());

        // use cisco id for now

        // storageServiceImplementation.store(documentFile); // commented out for the purpose of testing

        // convert form dao to the object
        Project project = Project.builder()
                .companyId(getCisco().getId())
                .title(projectDAO.getTitle())
                .description(projectDAO.getDescription())
                .skills(projectDAO.getSkills())
                .deadline(date)
                .location(projectDAO.getLocation())
                .documentName(documentFile != null ? documentFile.getOriginalFilename() : "a-file-name")
                .documentUrl("default")
                .build();


        companyServiceImplementation.postAProject(project);

        modelMap.addAttribute("compnayProject", project);
        modelMap.addAttribute("company", getCisco());
        modelMap.addAttribute("SessionData", getCisco().getEmail());

        return "redirect:/companyHomepage";
    }


    @PostMapping("/handle-company-project-update/{id}")
    public String updateCompanyProject(
            @PathVariable(name = "id") String projectId,
            @ModelAttribute ProjectDAO projectDAO,
            @RequestParam("documentFile") MultipartFile documentFile,
            ModelMap modelMap,
            HttpSession httpSession
    ){

        Long id = Long.parseLong(projectId);

        boolean updateFile = !documentFile.isEmpty(); // will change the way documents are handled

        Company company = companyServiceImplementation.updateCompany(id, companyDTO, updateFile,companyPhotoFile);
        modelMap.addAttribute("message", "update successful");

        modelMap.addAttribute("company", company);

        return "redirect:/companyProfilepage";

    }


    // will delete later - for dev purpose
    public Company getCisco(){
        Optional<Company> co =  companyRepository.findByEmailAndPassword("cisco@gmail.com", "cisco");
        return co.orElse(null);

    }
}
