package com.frankaboagye.connecthub.interfaces;

import com.frankaboagye.connecthub.daos.ProjectDAO;
import com.frankaboagye.connecthub.daos.ProjectUpdateDAO;
import com.frankaboagye.connecthub.entities.Project;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectServiceInterface {
    Project getProjectById(Long id);
    Project updateProject(ProjectUpdateDAO projectUpdateDAO, Long projectId, Long companyId, MultipartFile documentFile);
    Project updateProjectWithoutFile(ProjectUpdateDAO projectUpdateDAO, Long projectId, Long companyId);
    List<Project> getAllProjects();// change the wording
    List<Project> getAllProjectsByCompanyId(Long companyId);

}