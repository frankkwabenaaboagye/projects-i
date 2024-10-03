package com.frankaboagye.connecthub.interfaces;

import com.frankaboagye.connecthub.daos.ProjectDAO;
import com.frankaboagye.connecthub.entities.Project;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectServiceInterface {
    Project getProjectById(Long id);
    Project updateProject(ProjectDAO projectDAO, Long projectId, Long CompanyId, boolean updateFile, MultipartFile documentFile);
}