package com.frankaboagye.connecthub;

import com.frankaboagye.connecthub.entities.*;
import com.frankaboagye.connecthub.enums.ApplicationStatus;
import com.frankaboagye.connecthub.enums.Gender;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import com.frankaboagye.connecthub.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.frankaboagye.connecthub.enums.ApplicationStatus.ACCEPTED;

@SpringBootApplication
public class ConnectHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConnectHubApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(
            ConnectHubApplication app,
            CompanyRepository companyRepository,
            JobRepository jobRepository,
            FreelancerRepository freelancerRepository,
            ProjectDocumentRepository projectDocumentRepository,
            ProjectRepository projectRepository,
            ResumeRepository resumeRepository,
            JobApplicationRepository jobApplicationRepository,
            ProjectApplicationRepository projectApplicationRepository,
            StorageServiceInterface storageServiceImplementation
    ) {
        return args -> {
            createFileUploadDir(storageServiceImplementation);
            /*
            populateJobAndCompany(
                    companyRepository,
                    jobRepository,
                    freelancerRepository,
                    projectRepository,
                    projectDocumentRepository,
                    resumeRepository,
                    jobApplicationRepository,
                    projectApplicationRepository
            );
            */
        };
    }

    private void createFileUploadDir(StorageServiceInterface storageServiceImplementation){
        storageServiceImplementation.init();
    }

    private void populateJobAndCompany(
            CompanyRepository companyRepository,
            JobRepository jobRepository,
            FreelancerRepository freelancerRepository,
            ProjectRepository projectRepository,
            ProjectDocumentRepository projectDocumentRepository,
            ResumeRepository resumeRepository,
            JobApplicationRepository jobApplicationRepository,
            ProjectApplicationRepository projectApplicationRepository
    ) {

        System.out.println("Populating job and company");
        // Create a dummy company
        Company company = Company.builder()
                .email("techcorp@example.com")
                .name("TechCorp Solutions")
                .phonenumber("555-1234")
                .website("https://techcorp.com")
                .password("password123")
                .profilePictureLocation("techcorp_logo.png")
                .description("TechCorp Solutions is a leader in innovative tech services.")
                .address("123 Silicon Avenue, Tech City, Innovation Country")
                .build();

        // Save the company to the database
        company = companyRepository.save(company);

        // Create a dummy job associated with the company
        Job job = Job.builder()
                .company(company)
                .title("Software Engineer")
                .description("Responsible for developing and maintaining web applications.")
                .salary(75000.0)
                .skills(List.of("Java", "Spring Boot", "REST APIs", "Microservices"))
                .location("Remote")
                .deadline(LocalDate.of(2024, 12, 31))
                .moreInformation("This is more information")
                .associatedLabels(List.of("Full-time", "Remote"))
                .responsibilities(List.of("Develop new features", "Maintain the codebase", "Collaborate with teams"))
                .technologyInterests(List.of("Java", "Spring Boot", "Docker", "Kubernetes"))
                .build();


        Freelancer freelancer = Freelancer.builder()
                .email("john.doe@example.com")
                .gender(Gender.MALE) // Assuming Gender is an enum
                .fullName("John Doe")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .linkedin("https://linkedin.com/in/johndoe")
                .phoneNumber("555-123-4567")
                .education("Bachelor's in Computer Science")
                .profilepictureurl("https://example.com/images/johndoe.png")
                .basicCharge(50.0)
                .password("securePassword123")
                .skills(List.of("Java", "Spring Boot", "Microservices", "Docker"))
                .build();

        Resume resume = Resume.builder()
                .fileName("this_resume")
                .location("https://resume.pdf")
                .uploadDate(LocalDate.now())
                .title("resume_title")
                .description("resume_description")
                .isPrimary(true)
                .freelancer(freelancer)
                .build();

        freelancer.setResumes(List.of(resume));

        JobApplication jobApplication = JobApplication.builder()
                .resumeLocation("https://resume.pdf")
                .applicationDate(LocalDate.now())
                .status(ACCEPTED)
                .freelancer(freelancer)
                .company(company)
                .job(job)
                .freelancerComments("this is a comment")
                .coverLetter("this is a cover letter")
                .build();

        ProjectApplication projectApplication = ProjectApplication.builder()
                .resumeLocation("https://resume.pdf")
                .applicationDate(LocalDate.now())
                .status(ACCEPTED)
                .freelancer(freelancer)
                .company(company)
                .freelancerComment("this is a comment")
                .build();

        freelancer.setJobApplications(List.of(jobApplication));
        freelancer.setProjectApplications(List.of(projectApplication));


        Project project = Project.builder()
                .company(company)
                .title("AI-Powered Chatbot Development")
                .description("Develop an AI-powered chatbot to assist customers with inquiries and support.")
                .budget(20000.0)
                .skills(List.of("Artificial Intelligence", "Chatbot Development", "Natural Language Processing"))
                .deadline(LocalDate.of(2024, 12, 31))
                .location("Remote")
                .postedDate(LocalDate.now())
                .experienceLevels(List.of("Intermediate", "Expert"))
                .build();

        ProjectDocument projectDocument = ProjectDocument.builder()
                .project(project) // Associate the document with the project
                .documentName("ProjectProposal.pdf")
                .documentUrl("https://example.com/documents/ProjectProposal.pdf")
                .uploadDate(LocalDate.of(2024, 10, 12))
                .description("This document contains the initial proposal for the AI-powered chatbot project.")
                .isPrimary(true)
                .build();

        project.setProjectDocument(List.of(projectDocument));

        projectRepository.save(project);
        projectDocumentRepository.save(projectDocument);

        projectApplication.setProject(project);


        // Add the job to the company's job list
        company.getJobs().add(job);

        // Save the job to the database
        jobRepository.save(job);

        // Save the freelancer to the database
        freelancerRepository.save(freelancer);

        //save the resume
        resumeRepository.save(resume);

        // save the job application
        jobApplicationRepository.save(jobApplication);

        // save project application
        projectApplicationRepository.save(projectApplication);

        System.out.println("More Information \n" +  job.getMoreInformation());

        System.out.println("Dummy data populated: Company and Job created.");
    }

}
