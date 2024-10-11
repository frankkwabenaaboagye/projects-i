package com.frankaboagye.connecthub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long companyId;
    private String companyName;
    private String title;
    private String description;
    private Double salary;
    private String skills; // change the skills
    private LocalDate deadline;
    private String location;

    // job Type - Professional?
    // technology interest

//    @ElementCollection
//    private List<String> responsibilities;

}
