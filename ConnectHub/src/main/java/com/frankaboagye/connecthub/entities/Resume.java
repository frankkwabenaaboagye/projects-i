package com.frankaboagye.connecthub.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fileName;         // The name of the resume file
    private String location;         // Path where the resume is stored
    private LocalDate uploadDate;    // Date when the resume was uploaded
    private String title;            // Title of the resume, e.g., "Backend Developer Resume"
    private String description;      // Short description about this resume
    private Boolean isPrimary;       // Indicates if this is the freelancer's primary resume

    @ManyToOne
    @JoinColumn(name = "freelancer_id", nullable = false)
    private Freelancer freelancer;

    @Override
    public String toString() {
        return "Resume [id=" + id + ", fileName=" + fileName + ", location=" + location + "]";
    }

}
