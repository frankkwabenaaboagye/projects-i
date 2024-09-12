package com.frankaboagye.connecthub.repositories;

import com.frankaboagye.connecthub.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
