package com.frankaboagye.connecthub.services;

import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.entities.Project;
import com.frankaboagye.connecthub.interfaces.JobServiceInterface;
import com.frankaboagye.connecthub.interfaces.ProjectServiceInterface;
import com.frankaboagye.connecthub.repositories.JobRepository;
import com.frankaboagye.connecthub.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService implements ProjectServiceInterface {

    private final ProjectRepository projectRepository;


    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }
}
