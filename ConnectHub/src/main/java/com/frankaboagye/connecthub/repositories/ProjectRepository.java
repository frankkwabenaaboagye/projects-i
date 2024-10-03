package com.frankaboagye.connecthub.repositories;

import com.frankaboagye.connecthub.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByCompanyId(Long companyId);
    Project findByIdAndCompanyId(Long projectId, Long companyId);
}
