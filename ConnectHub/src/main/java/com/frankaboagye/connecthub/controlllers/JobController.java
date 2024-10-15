package com.frankaboagye.connecthub.controlllers;

import com.frankaboagye.connecthub.daos.JobDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.enums.GeneralSkills;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.JobServiceInterface;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.frankaboagye.connecthub.enums.ConnectHubConstant.PROFILE;
import static com.frankaboagye.connecthub.enums.ConnectHubConstant.SESSION_DATA;
import static com.frankaboagye.connecthub.enums.ConnectHubProfile.COMPANY;

@RequiredArgsConstructor
@Controller
public class JobController {

    private final CompanyServiceInterface companyServiceImplementation;
    private final JobServiceInterface jobServiceImplementation;


    @GetMapping("/view-job/{jobId}")
    public String viewJob(@PathVariable Long jobId, ModelMap modelMap, HttpSession httpSession) {

        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if (sessionData == null) {
            return "redirect:/login-company";
        }

        Company company = companyServiceImplementation.getCompanyById(sessionData).orElse(null);
        if (company == null) {
            return "redirect:/login-company";
        }

        Job job = jobServiceImplementation.getJobById(jobId);
        if (job == null) {
            return "redirect:/login-company";
        }

        // you can check for mismatch from of the company from the: jobId and the sessionID
        // we have to get a DTO for the company

        modelMap.addAttribute("company", company);
        modelMap.addAttribute("job", job);

        httpSession.setAttribute(SESSION_DATA.getDescription(), company.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(PROFILE.getDescription(), COMPANY.getValue());  // e.g. ("company", company)

        return "viewJob";
    }

    //
//    @GetMapping("/explore-jobs/{freelancerId}")
//    public String exploreJobs(@PathVariable Long freelancerId, ModelMap modelMap, HttpSession httpSession) {
//
//        // for dev-purpose - will change this
//        Freelancer freelancer = freelancerRepository.findById(freelancerId).orElse(null);
//        if(freelancer == null) {
//            return "redirect:/login-freelancer" + freelancerId;
//        }
//        modelMap.addAttribute("freelancer", freelancer);
//
//        List<Job> jobs  = jobServiceImplementation.getAllJobs();
//        modelMap.addAttribute("jobs", jobs);
//
//        httpSession.setAttribute(PROFILE.getDescription(), FREELANCER.getValue());
//        httpSession.setAttribute(SESSION_DATA.getDescription(), freelancer.getId());
//
//        return "/jobs/exploreJobsPage";
//
//    }
//
    @GetMapping("/post-a-job/{companyId}")
    public String postAJob(@PathVariable Long companyId, HttpSession httpSession, ModelMap modelMap) {


        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if (sessionData == null) {
            return "redirect:/login-company";
        }

        Company company = companyServiceImplementation.getCompanyById(sessionData).orElse(null);
        if (company == null) {
            return "redirect:/login-company";
        }


        modelMap.addAttribute("company", company);

        httpSession.setAttribute(SESSION_DATA.getDescription(), company.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(PROFILE.getDescription(), COMPANY.getValue());  // e.g. ("company", company)

        List<String> availableSkills = GeneralSkills.getAvailableSkills();
        modelMap.addAttribute("availableSkills", availableSkills);

        return "postJob";
    }

    @PostMapping("/handle-post-a-job")
    public String handleJobPosting(
            @ModelAttribute JobDAO jobDAO,
            ModelMap modelMap,
            HttpSession httpSession
    ) {
        // add securuty stuffs later, converstion stuffs

        Long sessionData = (Long) httpSession.getAttribute(SESSION_DATA.getDescription());
        if (sessionData == null) {
            return "redirect:/login-company";
        }

        Company company = companyServiceImplementation.getCompanyById(sessionData).orElse(null);
        if (company == null) {
            return "redirect:/login-company";
        }

        List<String> skillForJob = new ArrayList<>(jobDAO.getSkills());
        skillForJob.addAll(jobDAO.getOtherSkills()); // Add other skills to the existing list

        Job newJob = Job.builder()
                .company(company)
                .title(jobDAO.getTitle())
                .description(jobDAO.getDescription())
                .salary(Double.valueOf(jobDAO.getSalary()))
                .skills(skillForJob)
                .deadline(LocalDate.parse(jobDAO.getDeadline()))
                .location(jobDAO.getLocation())
                .moreInformation(jobDAO.getMoreInformation())
                .build();

        // associated labels, responsibilities, technology interest will be added - when company is updating the job;

        companyServiceImplementation.postAJob(newJob);

        modelMap.addAttribute("company", company);

        httpSession.setAttribute(SESSION_DATA.getDescription(), company.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(PROFILE.getDescription(), COMPANY.getValue());  // e.g. ("company", company)


        return "redirect:/company-homepage";
    }
//
//    @GetMapping("view-and-apply-job/{jobId}")
//    public String viewAndApplyJob(@PathVariable Long jobId, ModelMap modelMap, HttpSession httpSession){
//
//        Long sessionData = (Long) httpSession.getAttribute(FREELANCER.getValue());
//        if(sessionData == null){ return "redirect:/login-company"; }
//
//        Freelancer freelancer = freelancerRepository.findById(sessionData).orElse(null);
//        if(freelancer == null){return "redirect:/login-freelancer";}
//
//        String successMessage = (String) modelMap.get("successMessage");
//        modelMap.addAttribute("successMessage", successMessage);
//
//        modelMap.addAttribute("freelancer", freelancer);
//
//        Job job = jobServiceImplementation.getJobById(jobId);
//        Company company = job.getCompany();
//
//        if(company == null){return "redirect:/login-company";}
//
//        modelMap.addAttribute("company", company);
//        modelMap.addAttribute("job", job);
//
//        // company specific jobs
//        List<Job> companyJobs = jobServiceImplementation.getAllJobsByCompanyId(company.getId());
//        modelMap.addAttribute("companyJobs", companyJobs);
//
//        Path path = storageServiceImplementation.load(company.getProfilepicturename());
//        String profileSrc = MvcUriComponentsBuilder
//                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
//                .build()
//                .toUri()
//                .toString();
//        modelMap.addAttribute("profilePicturePath", profileSrc);
//
//        httpSession.setAttribute(PROFILE.getDescription(), FREELANCER.getValue());
//        httpSession.setAttribute(SESSION_DATA.getDescription(), freelancer.getId());
//
//
//        return "viewAndApplyJob";
//    }
//
}
