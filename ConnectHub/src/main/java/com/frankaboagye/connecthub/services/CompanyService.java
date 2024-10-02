package com.frankaboagye.connecthub.services;

import com.frankaboagye.connecthub.dtos.CompanyDTO;
import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.entities.Project;
import com.frankaboagye.connecthub.interfaces.CompanyServiceInterface;
import com.frankaboagye.connecthub.interfaces.StorageServiceInterface;
import com.frankaboagye.connecthub.repositories.CompanyRepository;
import com.frankaboagye.connecthub.repositories.JobRepository;
import com.frankaboagye.connecthub.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService implements CompanyServiceInterface {

    private final CompanyRepository companyRepository;
    private final StorageServiceInterface storageServiceImplementation;
    private final JobRepository jobRepository;
    private final ProjectRepository projectRepository;

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
    public Company updateCompany(Long id, CompanyDTO companyDTO, boolean updateFile, MultipartFile companyPhotoFile) {

        Optional<Company> companyOptional = companyRepository.findByIdAndEmail(id, companyDTO.getCompanyEmail());
        if(companyOptional.isEmpty()){return null;} // handle this well

        Company company = companyOptional.get();

        company.setName(companyDTO.getCompanyName());
        company.setPhonenumber(companyDTO.getCompanyPhonenumber());
        company.setWebsite(companyDTO.getCompanyWebsite());
        company.setDescription(companyDTO.getCompanyDescription());

        //profile picture
        if(updateFile){
            storageServiceImplementation.store(companyPhotoFile);
            company.setProfilepicturename(companyPhotoFile.getOriginalFilename());
        }


        return companyRepository.save(company);
    }


    @Override
    public void postAJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public void postAProject(Project project){
        projectRepository.save(project);
    }
}
