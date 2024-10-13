//package com.frankaboagye.connecthub.services;
//
//import com.frankaboagye.connecthub.entities.Job;
//import com.frankaboagye.connecthub.interfaces.JobServiceInterface;
//import com.frankaboagye.connecthub.repositories.JobRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class JobService implements JobServiceInterface {
//
//    private final JobRepository jobRepository;
//
//    @Override
//    public Job getJobById(Long Id) {
//        return jobRepository.findById(Id).orElse(null);
//    }
//
//
//    @Override
//    public List<Job> getAllJobs() {
//        return jobRepository.findAll();
//    }
//
//    @Override
//    public List<Job> getAllJobsByCompanyId(Long companyId) {
//        return jobRepository.findAllByCompany_Id(companyId);
//    }
//}
