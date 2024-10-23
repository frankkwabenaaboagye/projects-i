package com.frankaboagye.connecthub.interfaces;

import com.frankaboagye.connecthub.entities.Freelancer;

import java.util.Optional;

public interface FreelancerServiceInterface {

    void registerFreelancer(Freelancer freelancer);
    Optional<Freelancer> loginFreelancer(String email, String password);
    Optional<Freelancer> getFreelancerById(Long freelancerId);
    Freelancer updateFreelancer(Freelancer freelancer);
}