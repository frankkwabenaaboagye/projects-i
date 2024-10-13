package com.frankaboagye.connecthub.daos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


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
    private List<String> skills;
    private String password;
    private String confirmPassword;
}
