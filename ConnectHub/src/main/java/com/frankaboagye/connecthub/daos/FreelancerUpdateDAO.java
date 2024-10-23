package com.frankaboagye.connecthub.daos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreelancerUpdateDAO {

    private String fullName;
    private String linkedin;
    // private String email; // TODO: will be able to update email later
    private LocalDate dateOfBirth;
    private String education;
    private Double basicCharge;
    private String phoneNumber;
    private String gender;
    private List<String> skills;
    private String resumeTitle;


}
