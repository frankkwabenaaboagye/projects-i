package com.frankaboagye.connecthub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * The ProjectDocument entity represents documents associated with a project.
 * Each document may contain relevant information or details pertaining to the project.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProjectDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The name of the project document file.
     */
    private String documentName;

    /**
     * The URL where the project document is stored.
     */
    private String documentUrl;

    /**
     * The date when the project document was uploaded.
     */
    private LocalDate uploadDate;

    /**
     * A brief description of the project document.
     */
    private String description;

    /**
     * Indicates if this document is the primary document for the project.
     */
    private Boolean isPrimary;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project; // The project associated with this document
}

