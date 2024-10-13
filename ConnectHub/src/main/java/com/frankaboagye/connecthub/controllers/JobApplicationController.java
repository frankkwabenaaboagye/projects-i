package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.JobApplicationDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Freelancer;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.entities.JobApplication;
import com.frankaboagye.connecthub.enums.ApplicationStatus;
import com.frankaboagye.connecthub.enums.ConnectHubConstant;
import com.frankaboagye.connecthub.enums.ConnectHubProfile;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.JobApplicationServiceInterface;
import com.frankaboagye.connecthub.interfaces.JobServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import com.frankaboagye.connecthub.repositories.FreelancerRepository;
import com.frankaboagye.connecthub.repositories.JobRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.time.LocalDate;

import static com.frankaboagye.connecthub.enums.ApplicationStatus.SUBMITTED;
import static com.frankaboagye.connecthub.enums.ConnectHubConstant.*;
import static com.frankaboagye.connecthub.enums.ConnectHubProfile.FREELANCER;

@RequiredArgsConstructor
@Controller
public class JobApplicationController {

    private final JobServiceInterface jobServiceImplementation;
    private final CompanyRepository companyRepository;
    private final FreelancerRepository freelancerRepository;
    private final CompanyServiceInterface companyServiceImplementation;
    private final StorageServiceInterface storageServiceImplementation;
    private final JobRepository jobRepository;
    private final JobApplicationServiceInterface jobApplicationServiceImplementation;

    @PostMapping("/submit-job-application/{jobId}")
    public String handleJobApplicationSubmission(
            @ModelAttribute JobApplicationDAO jobApplicationDAO,
            @PathVariable long jobId,
            HttpSession httpSession,
            @RequestParam MultipartFile resumeFile,
            ModelMap modelMap,
            RedirectAttributes redirectAttributes
    ){

        //TODO

        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if(sessionData == null){
            return "redirect:/login-freelancer";
        }

        Freelancer freelancer = freelancerRepository.findById(sessionData).orElse(null);
        if(freelancer == null){
            return "redirect:/login-freelancer";
        }

        Job job = jobRepository.findById(jobId).orElse(null);

        if (job == null) {
            return "redirect:/login-freelancer";
        }

        Company company = job.getCompany();

        if(resumeFile.isEmpty()){
            return "redirect:/login-freelancer";
        }

        // resumeLocation
        storageServiceImplementation.store(resumeFile);
        Path resumePath = storageServiceImplementation.load(resumeFile.getOriginalFilename());

        JobApplication jobApplication = JobApplication.builder()
                .resumeLocation(resumePath.toString())
                .applicationDate(LocalDate.now())
                .status(SUBMITTED)
                .freelancer(freelancer)
                .company(company)
                .job(job)
                .build();


        jobApplication.setResumeLocation(resumePath.toString());

        jobApplicationServiceImplementation.submitJobApplication(jobApplication);


        modelMap.addAttribute("freelancer", freelancer);
        modelMap.addAttribute("job", job);
        modelMap.addAttribute("company", company);

        httpSession.setAttribute(PROFILE.getDescription(), FREELANCER.getValue());
        httpSession.setAttribute(SESSION_DATA.getDescription(), freelancer.getId());

        redirectAttributes.addFlashAttribute ("successMessage", "Success! Job Application submitted successfully.");


        return "redirect:/view-and-apply-job/" + job.getId();
    }


}
