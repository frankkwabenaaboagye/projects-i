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
    public Project updateProject(ProjectDAO projectDAO, Long projectId, Long CompanyId, boolean updateFile, MultipartFile documentFile) {

        // TODO: checks for the project id, company id, e.t.c
        Project project = projectRepository.findByIdAndCompanyId(projectId, CompanyId);

        project.setTitle(projectDAO.getTitle());
        project.setDescription(projectDAO.getDescription());
        project.setSkills(projectDAO.getSkills());
        project.setDeadline(LocalDate.parse(projectDAO.getDeadline()));
        project.setLocation(projectDAO.getLocation());
        project.setDocumentName(projectDAO.getDocumentName());
        project.setLocation(projectDAO.getLocation());

        // project document
        if(updateFile) {
            storageServiceImplementation.store(documentFile);
           Path filePath =  storageServiceImplementation.load(documentFile.getOriginalFilename());

            project.setDocumentUrl(filePath.toString());
        }

        return projectRepository.save(project);
    }
}
