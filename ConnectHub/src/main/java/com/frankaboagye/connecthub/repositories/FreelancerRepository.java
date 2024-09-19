package com.frankaboagye.connecthub.repositories;

import com.frankaboagye.connecthub.entities.Freelancer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FreelancerRepository extends JpaRepository<Freelancer, Long> {

    Optional<Freelancer> findByIdAndEmail(Long id, String email);

}
