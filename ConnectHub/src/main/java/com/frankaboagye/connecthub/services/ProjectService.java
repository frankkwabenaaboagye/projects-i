package com.frankaboagye.connecthub.services;

import com.frankaboagye.connecthub.daos.ProjectDAO;
import com.frankaboagye.connecthub.daos.ProjectUpdateDAO;
import com.frankaboagye.connecthub.entities.Project;
import com.frankaboagye.connecthub.entities.ProjectDocument;
import com.frankaboagye.connecthub.interfaces.ProjectServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import com.frankaboagye.connecthub.repositories.ProjectDocumentRepository;
import com.frankaboagye.connecthub.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class ProjectService implements ProjectServiceInterface {

    private final ProjectRepository projectRepository;
    private final StorageServiceInterface storageServiceImplementation;
    private final ProjectDocumentRepository projectDocumentRepository;


    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }


    @Override
    public Project updateProject(
            ProjectUpdateDAO projectUpdateDAO,
            Long projectId, Long companyId,
            MultipartFile projectFile
    ) {

        // TODO: checks for the project id, company id, e.t.c
        Project project = runUpdate(projectUpdateDAO, projectId, companyId);

        // project document
        storageServiceImplementation.store(projectFile);
        Path filePath =  storageServiceImplementation.load(projectFile.getOriginalFilename());

        ProjectDocument newProjectDocument = ProjectDocument.builder()
                .documentName(projectFile.getOriginalFilename())
                .documentUrl(filePath.toString())
                .uploadDate(LocalDate.now())
                .project(project)
                .build();

        project.setProjectDocument(List.of(newProjectDocument));

        Project saveProject = projectRepository.save(project);
        projectDocumentRepository.save(newProjectDocument);
        return saveProject;
    }


    @Override
    public Project updateProjectWithoutFile (ProjectUpdateDAO projectUpdateDAO, Long projectId, Long companyId) {

        // TODO: checks for the project id, company id, e.t.c
        return runUpdate(projectUpdateDAO, projectId, companyId);
    }


    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }


    @Override
    public List<Project> getAllProjectsByCompanyId(Long companyId) {
        return projectRepository.findAllByCompany_Id(companyId);
    }

    private Project runUpdate(ProjectUpdateDAO projectUpdateDAO, Long projectId, Long companyId){
        // run the update without the file
        Project project = projectRepository.findByIdAndCompany_Id(projectId, companyId);

        // TODO: null checks or use the Null Object Pattern

        Set<String> allSkills = new HashSet<>();
        allSkills.addAll(projectUpdateDAO.getSkills());
        allSkills.addAll(projectUpdateDAO.getOtherSkills());

        project.setTitle(projectUpdateDAO.getTitle());
        project.setDescription(projectUpdateDAO.getDescription());
        project.setBudget(Double.parseDouble(projectUpdateDAO.getBudget()));

        project.getSkills().addAll(allSkills);

        project.setDeadline(LocalDate.parse(projectUpdateDAO.getDeadline()));
        project.setLocation(projectUpdateDAO.getLocation());

        // handle the experience levels

        return projectRepository.save(project);

    }
}
