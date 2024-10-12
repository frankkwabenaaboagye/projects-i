package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.daos.JobApplicationDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Freelancer;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.entities.JobApplication;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.time.LocalDate;

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
            MultipartFile resumeFile,
            ModelMap modelMap,
            RedirectAttributes redirectAttributes
    ){

        //TODO

        Freelancer freelancer = (Freelancer) httpSession.getAttribute("freelancer");
        if (freelancer == null) {
            return "redirect:/login-freelancer";
        }

        Job job = jobRepository.findById(jobId).orElse(null);
        if (job == null) {
            return "redirect:/login-freelancer";
        }

        if(resumeFile.isEmpty()){
            return "redirect:/login-freelancer";
        }

        JobApplication jobApplication = JobApplication.builder()
                .fullName(jobApplicationDAO.getFullName())
                .email(jobApplicationDAO.getEmail())
                .linkedin(jobApplicationDAO.getLinkedin())
                .phoneNumber(jobApplicationDAO.getPhoneNumber())
                .applicationDate(LocalDate.now())
                .coverLetter(jobApplicationDAO.getCoverLetter())
                .build();

        // resumeLocation
        storageServiceImplementation.store(resumeFile);
        Path resumePath = storageServiceImplementation.load(resumeFile.getOriginalFilename());
        jobApplication.setResumeLocation(resumePath.toString());

        // company, Job
        jobApplication.setJob(job);

        Company company = companyRepository.findById(job.getCompanyId()).orElse(null);
        if(company == null){
            return "redirect:/login-company";
        }
        jobApplication.setCompany(company);

        jobApplicationServiceImplementation.submitJobApplication(jobApplication);


        modelMap.addAttribute("freelancer", freelancer);
        modelMap.addAttribute("job", job);
        modelMap.addAttribute("company", company);

        httpSession.setAttribute("freelancer", freelancer);
        httpSession.setAttribute("job", job);
        httpSession.setAttribute("company", company);

        redirectAttributes.addFlashAttribute ("successMessage", "Success! Job Application submitted successfully.");


        return "redirect:/view-and-apply-job/" + job.getId();
    }


}
