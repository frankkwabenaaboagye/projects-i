package com.frankaboagye.connecthub.services;//package com.frankaboagye.connecthub.services;
//
//import com.frankaboagye.connecthub.entities.Job;
//import com.frankaboagye.connecthub.entities.JobApplication;
//import com.frankaboagye.connecthub.interfaces.JobApplicationServiceInterface;
//import com.frankaboagye.connecthub.interfaces.JobServiceInterface;
//import com.frankaboagye.connecthub.repositories.JobApplicationRepository;
//import com.frankaboagye.connecthub.repositories.JobRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class JobApplicationService implements JobApplicationServiceInterface {
//
//    private final JobApplicationRepository jobApplicationRepository;
//
//
//    @Override
//    public void submitJobApplication(JobApplication jobApplication) {
//        System.out.println("saving job application....");
//        jobApplicationRepository.save(jobApplication);
//    }
//}
