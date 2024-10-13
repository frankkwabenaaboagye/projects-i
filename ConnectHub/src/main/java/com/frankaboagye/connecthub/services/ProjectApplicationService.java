package com.frankaboagye.connecthub.services;

import com.frankaboagye.connecthub.entities.JobApplication;
import com.frankaboagye.connecthub.entities.ProjectApplication;
import com.frankaboagye.connecthub.interfaces.JobApplicationServiceInterface;
import com.frankaboagye.connecthub.interfaces.ProjectApplicationServiceInterface;
import com.frankaboagye.connecthub.repositories.JobApplicationRepository;
import com.frankaboagye.connecthub.repositories.ProjectApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectApplicationService implements ProjectApplicationServiceInterface {

    private final ProjectApplicationRepository projectApplicationRepository;


    @Override
    public void submitProjectApplication(ProjectApplication projectApplication) {
        System.out.println("saving project application....");
        projectApplicationRepository.save(projectApplication);
    }
}
