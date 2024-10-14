//package com.frankaboagye.connecthub.repositories;
//
//import com.frankaboagye.connecthub.entities.Project;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface ProjectRepository extends JpaRepository<Project, Long> {
//    List<Project> findAllByCompany_Id(Long companyId);
//    Project findByIdAndCompany_Id(Long projectId, Long companyId);
//}
