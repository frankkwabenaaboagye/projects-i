package com.frankaboagye.connecthub.controlllers;

import com.frankaboagye.connecthub.daos.ProjectDAO;
import com.frankaboagye.connecthub.daos.ProjectUpdateDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Project;
import com.frankaboagye.connecthub.entities.ProjectDocument;
import com.frankaboagye.connecthub.enums.GeneralSkills;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.ProjectServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
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
import java.util.*;

import static com.frankaboagye.connecthub.enums.ConnectHubConstant.PROFILE;
import static com.frankaboagye.connecthub.enums.ConnectHubConstant.SESSION_DATA;
import static com.frankaboagye.connecthub.enums.ConnectHubProfile.COMPANY;
import static com.frankaboagye.connecthub.enums.ConnectHubProfile.FREELANCER;

@RequiredArgsConstructor
@Controller
public class ProjectController {

    private final CompanyServiceInterface companyServiceImplementation;
    private final StorageServiceInterface storageServiceImplementation;
    private final ProjectServiceInterface projectServiceImplementation;


    @GetMapping("/view-project/{projectId}")
    public String viewProject(
            @PathVariable Long projectId,
            ModelMap modelMap,
            HttpSession httpSession
    ) {

        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if (sessionData == null) { return "redirect:/login-company"; }

        Company company = companyServiceImplementation.getCompanyById(sessionData).orElse(null);
        if (company == null) { return "redirect:/login-company"; }

        Project project = projectServiceImplementation.getProjectById(projectId);

        modelMap.addAttribute("company", company);
        modelMap.addAttribute("project", project);

        Path path = Paths.get(project.getProjectDocument().getFirst().getDocumentUrl());

        modelMap.addAttribute("documentSrc", getDocumentSrc(path));

        return "viewProject";
    }


    @GetMapping("/post-a-project/{companyId}")
    public String postAProject(
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

        List<String> availableSkills = GeneralSkills.getAvailableSkills();
        modelMap.addAttribute("availableSkills", availableSkills);




        httpSession.setAttribute(SESSION_DATA.getDescription(), company.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(PROFILE.getDescription(), COMPANY.getValue());  // e.g. ("company", company)

        return "postProject";
    }

    @PostMapping("/handle-post-a-project")
    public String handleProjectPosting(
            @ModelAttribute ProjectDAO projectDAO,
            ModelMap modelMap,
            HttpSession httpSession,
            @RequestParam MultipartFile projectFile
    ) {
        // add security stuffs later, conversion stuffs

        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if (sessionData == null) {
            return "redirect:/login-company";
        }

        Company company = companyServiceImplementation.getCompanyById(sessionData).orElse(null);
        if (company == null) {
            return "redirect:/login-company";
        }

        // handle a situation, where the file is empty

        storageServiceImplementation.store(projectFile);
        Path documentPath = storageServiceImplementation.load(projectFile.getOriginalFilename());

        ProjectDocument newProjectDocument = ProjectDocument.builder()
                .documentName(projectFile.getOriginalFilename())
                .documentUrl(documentPath.toString())
                .uploadDate(LocalDate.now())
                .build();

        // Skills
        Set<String> projectSkills = new HashSet<>(projectDAO.getSkills());
        projectSkills.addAll(projectDAO.getOtherSkills());

        // convert form dao to the object
        Project project = Project.builder()
                .company(company)
                .title(projectDAO.getTitle())
                .description(projectDAO.getDescription())
                .budget(Double.valueOf(projectDAO.getBudget()))
                .skills(projectSkills)
                .deadline(LocalDate.parse(projectDAO.getDeadline()))
                .location(projectDAO.getLocation())
                .postedDate(LocalDate.now())
                .build();


        // experience levels and other fields will be added during updates

        companyServiceImplementation.postAProject(project, newProjectDocument);

        // find the project here, and add it to the modelMap

        modelMap.addAttribute("project", project);
        modelMap.addAttribute("company", company);

        httpSession.setAttribute(SESSION_DATA.getDescription(), company.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(PROFILE.getDescription(), COMPANY.getValue());  // e.g. ("company", company)


        return "redirect:/company-homepage";


    }


    @PostMapping("/handle-company-project-update/{projectId}")
    public String updateCompanyProject(
            @PathVariable Long projectId,
            @ModelAttribute ProjectUpdateDAO projectUpdateDAO,
            @RequestParam MultipartFile projectFile,
            ModelMap modelMap,
            HttpSession httpSession
    ) {

        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if (sessionData == null) { return "redirect:/login-company"; }

        Company company = companyServiceImplementation.getCompanyById(sessionData).orElse(null);
        if (company == null) { return "redirect:/login-company"; }

        Project project = null;

        if (projectFile.isEmpty()) {
            project = projectServiceImplementation.updateProjectWithoutFile(projectUpdateDAO , projectId, company.getId() );
        } else {
            project = projectServiceImplementation.updateProject(projectUpdateDAO ,  projectId, company.getId(), projectFile);
            Path path = Paths.get(project.getProjectDocument().getFirst().getDocumentUrl());
            modelMap.addAttribute("documentSrc", getDocumentSrc(path));

        }

        modelMap.addAttribute("message", "update successful");
        modelMap.addAttribute("project", project);

        httpSession.setAttribute(SESSION_DATA.getDescription(), company.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(PROFILE.getDescription(), FREELANCER.getValue());  // e.g. ("company", company)


        return "redirect:/view-project/" + projectId;

    }
//
//
//    @GetMapping("/explore-projects/{freelancerId}")
//    public String exploreJobs(@PathVariable Long freelancerId, ModelMap modelMap, HttpSession httpSession) {
//
//        // for dev-purpose - will change this
//        //TODO
//        Freelancer freelancer = freelancerRepository.findById(freelancerId).orElse(null);
//        modelMap.addAttribute("freelancer", freelancer);
//        httpSession.setAttribute("freelancer", freelancer);
//
//        List<Project> projects = projectServiceImplementation.getAllProjects();
//        modelMap.addAttribute("projects", projects);
//
//        return "/projects/exploreProjectsPage";
//
//    }
//
//    @GetMapping("view-and-apply-project/{projectId}")
//    public String viewAndApplyProject(
//            @PathVariable Long projectId,
//            ModelMap modelMap,
//            HttpSession httpSession
//    ) {
//
//        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription()); // this is the id of the freelancer
//
//        if (sessionData == null) {
//            return "redirect:/login-freelancer";
//        }
//
//        Freelancer freelancer = freelancerRepository.findById(sessionData).orElse(null);
//        if (freelancer == null) {
//            return "redirect:/login-freelancer";
//        }
//
//        modelMap.addAttribute("freelancer", freelancer);
//
//        Project project = projectServiceImplementation.getProjectById(projectId);
//        if (project == null) {
//            return "redirect:/login-company";
//        }
//
//        Company company = project.getCompany();
//
//        modelMap.addAttribute("company", company);
//        modelMap.addAttribute("project", project);
//
//        // company specific projects
//        List<Project> companyProjects = projectServiceImplementation.getAllProjectsByCompanyId(company.getId());
//        modelMap.addAttribute("companyProjects", companyProjects);
//
//        Path path = storageServiceImplementation.load(company.getProfilepicturename());
//        String profileSrc = MvcUriComponentsBuilder
//                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
//                .build()
//                .toUri()
//                .toString();
//        modelMap.addAttribute("profilePicturePath", profileSrc);
//
//        Path documentPath = Paths.get(project.getProjectDocument().getDocumentUrl());
//        modelMap.addAttribute("documentSrc", getDocumentSrc(documentPath));
//
//        return "viewAndApplyProject";
//
//    }
//
//
    public String getDocumentSrc(Path path) {

        return MvcUriComponentsBuilder
                .fromMethodName(FileUploadController.class, "displayFile", path.getFileName().toString())
                .build()
                .toUri()
                .toString();
    }
}