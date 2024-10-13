package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.ProjectDAO;
import com.frankaboagye.connecthub.entities.*;
import com.frankaboagye.connecthub.enums.GeneralSkills;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.ProjectServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import com.frankaboagye.connecthub.repositories.FreelancerRepository;
import com.frankaboagye.connecthub.repositories.ProjectDocumentRepository;
import com.frankaboagye.connecthub.repositories.ProjectRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.frankaboagye.connecthub.enums.ConnectHubConstant.CONNECT_HUB_PROFILE;
import static com.frankaboagye.connecthub.enums.ConnectHubConstant.CONNECT_HUB_SESSION_DATA;
import static com.frankaboagye.connecthub.enums.ConnectHubProfile.COMPANY;

@RequiredArgsConstructor
@Controller
public class ProjectController {

    private final ProjectServiceInterface projectServiceImplementation;
    private final CompanyServiceInterface companyServiceImplementation;
    private final StorageServiceInterface storageServiceImplementation;
    private final CompanyRepository companyRepository;
    private final ProjectRepository projectRepository;
    private final FreelancerRepository freelancerRepository;
    private final ProjectDocumentRepository projectDocumentRepository;

    @GetMapping("/view-project/{id}")
    public String viewProject(
            @PathVariable Long id,
            ModelMap modelMap,
            HttpSession httpSession
    ) {

        String sessionData = (String) httpSession.getAttribute(CONNECT_HUB_SESSION_DATA.getDescription());
        if (sessionData == null) {
            return "redirect:/login-company";
        }

        Company company = companyRepository.findById(Long.parseLong(sessionData)).orElse(null);

        if (company == null) {
            return "redirect:/login-company";
        }

        Project project = projectServiceImplementation.getProjectById(id);

        modelMap.addAttribute("company", company);
        modelMap.addAttribute("project", project);

        Path path = Paths.get(project.getProjectDocument().getDocumentUrl());

        modelMap.addAttribute("documentSrc", getDocumentSrc(path));

        return "viewProject";
    }


    @GetMapping("/post-a-project/{companyId}")
    public String postAProject(@PathVariable Long companyId, HttpSession httpSession, ModelMap modelMap) {
        Optional<Company> companyOptional = companyRepository.findById(companyId);

        if (companyOptional.isEmpty()) {
            return "redirect:/login-company";
        }

        Company company = companyOptional.get();
        modelMap.addAttribute("company", company);

        List<String> availableSkills = GeneralSkills.getAvailableSkills();
        modelMap.addAttribute("availableSkills", availableSkills );


        httpSession.setAttribute(CONNECT_HUB_SESSION_DATA.getDescription(), company.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(CONNECT_HUB_PROFILE.getDescription(), COMPANY.getValue());  // e.g. ("company", company)

        return "postProject";
    }

    @PostMapping("/handle-post-a-project")
    public String handleProjectPosting(
            @ModelAttribute ProjectDAO projectDAO,
            ModelMap modelMap,
            HttpSession httpSession,
            @RequestParam MultipartFile projectFile
    ) {
        // add securuty stuffs later, converstion stuffs

        String sessionData = (String) httpSession.getAttribute(CONNECT_HUB_SESSION_DATA.getDescription());
        Company company = companyRepository.findById(Long.parseLong(sessionData)).orElse(null);

        if (company == null) {
            return "redirect:/login-company";
        }

        storageServiceImplementation.store(projectFile);
        Path documentPath = storageServiceImplementation.load(projectFile.getOriginalFilename());

        ProjectDocument newProjectDocument = ProjectDocument.builder()
                .documentName(projectFile.getOriginalFilename())
                .documentUrl(documentPath.toString())
                .uploadDate(LocalDate.now())
                .build();

        // Skills
        List<String> allSkills = new ArrayList<>(projectDAO.getSkills());
        allSkills.addAll(projectDAO.getOtherSkills());

        // convert form dao to the object
        Project project = Project.builder()
                .company(company)
                .title(projectDAO.getTitle())
                .description(projectDAO.getDescription())
                .budget(Double.valueOf(projectDAO.getBudget()))
                .skills(allSkills)
                .deadline(LocalDate.parse(projectDAO.getDeadline()))
                .location(projectDAO.getLocation())
                .projectDocument(newProjectDocument)
                .postedDate(LocalDate.now())
                .build();

        // experience levels and other fields will be added during updates

        companyServiceImplementation.postAProject(project);


        modelMap.addAttribute("project", project);
        modelMap.addAttribute("company", company);

        httpSession.setAttribute(CONNECT_HUB_SESSION_DATA.getDescription(), company.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(CONNECT_HUB_PROFILE.getDescription(), COMPANY.getValue());  // e.g. ("company", company)


        return "redirect:/companyHomepage";


    }


    @PostMapping("/handle-company-project-update/{id}")
    public String updateCompanyProject(
            @PathVariable(name = "id") String projectId,
            @ModelAttribute ProjectDAO projectDAO,
            @RequestParam("documentFile") MultipartFile documentFile,
            ModelMap modelMap,
            HttpSession httpSession
    ) {

        Long id = Long.parseLong(projectId);

        boolean updateFile = !documentFile.isEmpty(); // will change the way documents are handled

        Project project = null;

        projectDAO.setCompanyId(String.valueOf(getCisco().getId()));

        if (documentFile.isEmpty()) {
            project = projectServiceImplementation.updateProjectWithoutFile(projectDAO, Long.parseLong(projectId), getCisco().getId());
        } else {
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
    public String viewAndApplyProject(@PathVariable Long projectId, ModelMap modelMap, HttpSession httpSession) {

        Freelancer freelancer = (Freelancer) httpSession.getAttribute("freelancer");
        if (freelancer == null) {
            return "redirect:/login-freelancer";
        }

        modelMap.addAttribute("freelancer", freelancer);

        Project project = projectServiceImplementation.getProjectById(projectId);
        Company company = companyRepository.findById(project.getCompanyId()).orElse(null);

        if (company == null) {
            return "redirect:/login-company";
        }

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
    public Company getCisco() {
        Optional<Company> co = companyRepository.findByEmailAndPassword("cisco@gmail.com", "cisco");
        return co.orElse(null);

    }

    public String getDocumentSrc(Path path) {

        return MvcUriComponentsBuilder
                .fromMethodName(FileUploadController.class, "displayFile", path.getFileName().toString())
                .build()
                .toUri()
                .toString();
    }
}
