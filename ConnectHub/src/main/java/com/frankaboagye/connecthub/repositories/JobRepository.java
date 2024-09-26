package com.frankaboagye.connecthub.repositories;

import com.frankaboagye.connecthub.entities.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {

}
