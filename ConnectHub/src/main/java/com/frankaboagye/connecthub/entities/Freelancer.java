package com.frankaboagye.connecthub.entities;

import com.frankaboagye.connecthub.enums.Gender;
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
public class Freelancer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)  // Store as STRING in the database
    private Gender gender;

    private String fullName;
    private LocalDate dateOfBirth;
    private String linkedin;
    private String phoneNumber;
    private String education;
    private String profilepicturename;
    private Double basicCharge;
    private String password;


    @ElementCollection
    @CollectionTable(name = "freelancer_skills", joinColumns = @JoinColumn(name = "freelancer_id"))
    @Column(name = "skill")
    private List<String> skills;

    /*
     Hibernate will manage the creation of both the primary Freelancer table and the additional freelancer_skills table automatically.
     */

}
