package com.frankaboagye.connecthub.services;

import com.frankaboagye.connecthub.daos.ProjectDAO;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.entities.Project;
import com.frankaboagye.connecthub.interfaces.JobServiceInterface;
import com.frankaboagye.connecthub.interfaces.ProjectServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import com.frankaboagye.connecthub.repositories.JobRepository;
import com.frankaboagye.connecthub.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService implements ProjectServiceInterface {

    private final ProjectRepository projectRepository;
    private final StorageServiceInterface storageServiceImplementation;


    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }


    @Override
    public Project updateProject(ProjectDAO projectDAO, Long projectId, Long companyId, MultipartFile documentFile) {

        // TODO: checks for the project id, company id, e.t.c
        Project project = runUpdate(projectDAO, projectId, companyId);

        // project document
        storageServiceImplementation.store(documentFile);
        Path filePath =  storageServiceImplementation.load(documentFile.getOriginalFilename());
        project.getProjectDocument().setDocumentUrl(filePath.toString());
        project.getProjectDocument().setDocumentName(documentFile.getOriginalFilename());

        return projectRepository.save(project);
    }


    @Override
    public Project updateProjectWithoutFile (ProjectDAO projectDAO, Long projectId, Long companyId) {

        // TODO: checks for the project id, company id, e.t.c
        return runUpdate(projectDAO, projectId, companyId);
    }


    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }


    @Override
    public List<Project> getAllProjectsByCompanyId(Long companyId) {
        return projectRepository.findAllByCompany_Id(companyId);
    }

    private Project runUpdate(ProjectDAO projectDAO, Long projectId, Long companyId){

        Project project = projectRepository.findByIdAndCompany_Id(projectId, companyId);

        // TODO: null checks or use the Null Object Pattern

        project.setTitle(projectDAO.getTitle());
        project.setDescription(projectDAO.getDescription());
        project.setSkills(projectDAO.getSkills());
        project.setDeadline(LocalDate.parse(projectDAO.getDeadline()));
        project.setLocation(projectDAO.getLocation());

        // set the project document

        project.setLocation(projectDAO.getLocation());

        return projectRepository.save(project);

    }
}
