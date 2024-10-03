package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.ProjectDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Project;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.ProjectServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import com.frankaboagye.connecthub.repositories.ProjectRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class ProjectController {

    private final ProjectServiceInterface projectServiceImplementation;
    private final CompanyServiceInterface companyServiceImplementation;
    private final StorageServiceInterface storageServiceImplementation;
    private final CompanyRepository companyRepository;
    private final ProjectRepository projectRepository;

    @GetMapping("/view-project/{id}")
    public String viewProject(@PathVariable Long id, ModelMap modelMap){

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

        storageServiceImplementation.store(documentFile); // commented out for the purpose of testing
        Path filePath = storageServiceImplementation.load(documentFile.getOriginalFilename()); // will make this better  later

        // convert form dao to the object
        Project project = Project.builder()
                .companyId(getCisco().getId())
                .title(projectDAO.getTitle())
                .description(projectDAO.getDescription())
                .skills(projectDAO.getSkills())
                .deadline(date)
                .location(projectDAO.getLocation())
                .documentName(documentFile.getOriginalFilename())
                .documentUrl(filePath.toString())
                .build();


        companyServiceImplementation.postAProject(project);

        modelMap.addAttribute("companyProject", project);
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

        Project project = projectServiceImplementation.updateProject(projectDAO, id, Long.parseLong(projectDAO.getCompanyId()), updateFile, documentFile);
        modelMap.addAttribute("message", "update successful");

        modelMap.addAttribute("project", project);

        return "redirect:/view-project/" + id;

    }


    // will delete later - for dev purpose
    public Company getCisco(){
        Optional<Company> co =  companyRepository.findByEmailAndPassword("cisco@gmail.com", "cisco");
        return co.orElse(null);

    }
}
