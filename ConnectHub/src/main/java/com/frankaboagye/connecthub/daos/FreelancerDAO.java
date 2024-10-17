package com.frankaboagye.connecthub.daos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreelancerDAO {

    private String fullName;
    private String email;
    private String dateOfBirth;
    private String linkedin;
    private String phoneNumber;
    private String education;
    private String basicCharge;
    private String gender;
    private Set<String> skills;
    private String password;
    private String confirmPassword;
}
