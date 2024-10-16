package com.frankaboagye.connecthub.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Project entity represents a project posted by a company.
 * Freelancers can apply to these projects based on their skills and experience.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Project {

    /**
     * The unique ID of the project.
     * Auto-generated by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The company offering the job.
     * This creates a many-to-one relationship with the Company entity.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id", nullable = false) // Foreign key column in the Job table
    private Company company;


    /**
     * The title of the project.
     */
    private String title;

    /**
     * A detailed description of the project.
     */
    private String description;

    /**
     * The estimated budget for the project.
     * This indicates the financial compensation offered for completing the project.
     */
    private Double budget;

    /**
     * A list of required skills for the project.
     * This helps freelancers identify if they are qualified to apply.
     */
    @ElementCollection
    @CollectionTable(name = "project_skills", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "skill")
    @Builder.Default
    private Set<String> skills = new HashSet<>();

    /**
     * The deadline for project completion.
     */
    private LocalDate deadline;

    /**
     * The location where the project is based or if it is remote.
     */
    private String location;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectDocument> projectDocument = new ArrayList<>(); // This can be null if no documents are associated


    /**
     * The date when the project was posted.
     */
    private LocalDate postedDate;

    /**
     * A list of required experience levels for freelancers applying to the project.
     * This can help filter applicants based on their qualifications.
     */
    @ElementCollection
    @CollectionTable(name = "project_experience_levels", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "experience_level")
    @Builder.Default
    private Set<String> experienceLevels = new HashSet<>();

    @Override
    public String toString() {
        return "Project [id=" + getId() + ", title=" + getTitle() + "]";
    }
}
