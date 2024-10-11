package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.ProjectDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Freelancer;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.entities.Project;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.ProjectServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import com.frankaboagye.connecthub.repositories.FreelancerRepository;
import com.frankaboagye.connecthub.repositories.ProjectRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class ProjectController {

    private final ProjectServiceInterface projectServiceImplementation;
    private final CompanyServiceInterface companyServiceImplementation;
    private final StorageServiceInterface storageServiceImplementation;
    private final CompanyRepository companyRepository;
    private final ProjectRepository projectRepository;
    private final FreelancerRepository freelancerRepository;

    @GetMapping("/view-project/{id}")
    public String viewProject(@PathVariable Long id, ModelMap modelMap){

        Project project = projectServiceImplementation.getProjectById(id);
        Company company = companyRepository.findById(project.getCompanyId()).orElse(null);

        modelMap.addAttribute("company", company);
        modelMap.addAttribute("project", project);
        modelMap.addAttribute("documentSrc", getDocumentSrc(project.getDocumentName()));

        return "viewProject";
    }


    @GetMapping("/post-a-project/{companyId}")
    public String postAProject(@PathVariable Long companyId, HttpSession httpSession, ModelMap modelMap){
        Optional<Company> companyOptional = companyRepository.findById(companyId);

        if(companyOptional.isEmpty()){return "redirect:/login-company";}

        Company company = companyOptional.get();
        modelMap.addAttribute("company", company);

        httpSession.setAttribute("companyData", company);
        httpSession.setAttribute("sessionData", company.getEmail());

        return "postProject";
    }

    @PostMapping("/handle-post-a-project")
    public String handleProjectPosting(@ModelAttribute ProjectDAO projectDAO, ModelMap modelMap, HttpSession httpSession, @RequestParam("documentFile") MultipartFile documentFile) {
        // add securuty stuffs later, converstion stuffs

        String sessionData = (String) httpSession.getAttribute("sessionData");
        Company companyData = (Company) httpSession.getAttribute("companyData");

        if(companyData != null && sessionData != null){
            String stop = "here";

            var date = LocalDate.parse(projectDAO.getDeadline());

            // use cisco id for now

            storageServiceImplementation.store(documentFile); // commented out for the purpose of testing
            Path filePath = storageServiceImplementation.load(documentFile.getOriginalFilename()); // will make this better  later

            // convert form dao to the object
            Project project = Project.builder()
                    .companyId(companyData.getId())
                    .companyName(companyData.getName())
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
            modelMap.addAttribute("company", companyData);

            httpSession.setAttribute("sessionData", sessionData);
            httpSession.setAttribute("companyData", companyData);


            return "redirect:/companyHomepage";
        }

        return "redirect:/login-company";

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

        Project project = null;

        projectDAO.setCompanyId(String.valueOf(getCisco().getId()));

        if(documentFile.isEmpty()){
            project = projectServiceImplementation.updateProjectWithoutFile(projectDAO, Long.parseLong(projectId), getCisco().getId());
        }else{
            project = projectServiceImplementation.updateProject(projectDAO, id, getCisco().getId(), documentFile);
            modelMap.addAttribute("documentSrc", getDocumentSrc(documentFile.getOriginalFilename()));

        }



        modelMap.addAttribute("message", "update successful");

        modelMap.addAttribute("project", project);



        return "redirect:/view-project/" + id;

    }


    @GetMapping("/explore-projects/{freelancerId}")
    public String exploreJobs(@PathVariable Long freelancerId, ModelMap modelMap, HttpSession httpSession) {

        // for dev-purpose - will change this
        //TODO
        Freelancer freelancer = freelancerRepository.findById(freelancerId).orElse(null);
        modelMap.addAttribute("freelancer", freelancer);
        httpSession.setAttribute("freelancer", freelancer);

        List<Project> projects = projectServiceImplementation.getAllProjects();
        modelMap.addAttribute("projects", projects);

        return "/projects/exploreProjectsPage";

    }

    @GetMapping("view-and-apply-project/{projectId}")
    public String viewAndApplyProject(@PathVariable Long projectId, ModelMap modelMap, HttpSession httpSession){

        Freelancer freelancer = (Freelancer) httpSession.getAttribute("freelancer");
        if(freelancer == null){return "redirect:/login-freelancer";}

        modelMap.addAttribute("freelancer", freelancer);

        Project project = projectServiceImplementation.getProjectById(projectId);
        Company company = companyRepository.findById(project.getCompanyId()).orElse(null);

        if(company == null){return "redirect:/login-company";}

        modelMap.addAttribute("company", company);
        modelMap.addAttribute("project", project);

        // company specific projects
        List<Project> companyProjects = projectServiceImplementation.getAllProjectsByCompanyId(company.getId());
        modelMap.addAttribute("companyProjects", companyProjects);

        Path path = storageServiceImplementation.load(company.getProfilepicturename());
        String profileSrc = MvcUriComponentsBuilder
                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                .build()
                .toUri()
                .toString();
        modelMap.addAttribute("profilePicturePath", profileSrc);

        modelMap.addAttribute("documentSrc", getDocumentSrc(project.getDocumentName()));



        return "viewAndApplyProject";

    }


    // will delete later - for dev purpose.
    public Company getCisco(){
        Optional<Company> co =  companyRepository.findByEmailAndPassword("cisco@gmail.com", "cisco");
        return co.orElse(null);

    }

    public String getDocumentSrc(String filename){
        Path path = storageServiceImplementation.load(filename);
        return MvcUriComponentsBuilder
                .fromMethodName(FileUploadController.class, "displayFile", path.getFileName().toString())
                .build()
                .toUri()
                .toString();
    }
}
