package com.frankaboagye.connecthub.interfaces;

import com.frankaboagye.connecthub.daos.ProjectDAO;
import com.frankaboagye.connecthub.entities.Project;
import jakarta.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectServiceInterface {
    Project getProjectById(Long id);
    Project updateProject(ProjectDAO projectDAO, Long projectId, Long companyId, MultipartFile documentFile);
    Project updateProjectWithoutFile(ProjectDAO projectDAO, Long projectId, Long companyId);
    List<Project> getAllProjects();// change the wording

}