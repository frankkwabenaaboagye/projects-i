package com.frankaboagye.connecthub.services;

import com.frankaboagye.connecthub.daos.CompanyUpdateDAO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.entities.Project;
import com.frankaboagye.connecthub.entities.ProjectDocument;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import com.frankaboagye.connecthub.repositories.JobRepository;
import com.frankaboagye.connecthub.repositories.ProjectDocumentRepository;
import com.frankaboagye.connecthub.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService implements CompanyServiceInterface {

    private final CompanyRepository companyRepository;
    private final StorageServiceInterface storageServiceImplementation;
    private final JobRepository jobRepository;
    private final ProjectRepository projectRepository;
    private final ProjectDocumentRepository projectDocumentRepository;

    @Override
    public void registerCompany(Company company) {
        // use aop here
        System.out.println("Registering company " + company.getName());
        companyRepository.save(company);
    }

    @Override
    public Optional<Company> loginCompany(String email, String password) {
        // ustilise aop, security e.t.c
        return companyRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public Optional<Company> getCompanyById(Long companyId) {
        return companyRepository.findById(companyId);
    }

    @Override
    public Company updateCompany(Long id, CompanyUpdateDAO companyUpdateDAO, boolean updateFile, MultipartFile companyPhotoFile) {

        Optional<Company> companyOptional = companyRepository.findByIdAndEmail(id, companyUpdateDAO.getEmail());
        if(companyOptional.isEmpty()){return null;} // handle this well

        Company company = companyOptional.get();

        // handle this well
        company.setName(companyUpdateDAO.getName());
        company.setPhonenumber(companyUpdateDAO.getPhonenumber());
        company.setWebsite(companyUpdateDAO.getWebsite());
        company.setDescription(companyUpdateDAO.getDescription());
        company.setAddress(companyUpdateDAO.getAddress());

        //profile picture
        if(updateFile){
            storageServiceImplementation.store(companyPhotoFile);
            Path photoPath = storageServiceImplementation.load(companyPhotoFile.getOriginalFilename());
//            company.setProfilePictureLocation(photoPath.toFile().getAbsolutePath());
            company.setProfilePictureLocation(photoPath.toString());
        }
        return companyRepository.save(company);
    }


    @Transactional
    @Override
    public void postAJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public void postAProject(Project project, ProjectDocument projectDocument){

        Project savedProject = projectRepository.save(project);

        projectDocument.setProject(savedProject);
        projectDocumentRepository.save(projectDocument);

    }
}
