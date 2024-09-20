package com.frankaboagye.connecthub.services;

import com.frankaboagye.connecthub.entities.Freelancer;
import com.frankaboagye.connecthub.interfaces.FreelancerServiceInterface;
import com.frankaboagye.connecthub.repositories.FreelancerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FreelancerService implements FreelancerServiceInterface {

    private final FreelancerRepository freelancerRepository;

    @Override
    public void registerFreelanceer(Freelancer freelancer) {

        System.out.println("Registering freelancer: " + freelancer.getFullName());
        freelancerRepository.save(freelancer);
    }

    @Override
    public Optional<Freelancer> loginFreelancer(String email, String password) {
        return freelancerRepository.findByEmailAndPassword(email, password);
    }
}
