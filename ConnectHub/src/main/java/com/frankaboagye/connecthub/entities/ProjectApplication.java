package com.frankaboagye.connecthub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProjectApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String resumeLocation;
    private LocalDate applicationDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;


    @ManyToOne(optional = false) // optional = false ensures a JobApplication must have a Job and a Company
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;


}
