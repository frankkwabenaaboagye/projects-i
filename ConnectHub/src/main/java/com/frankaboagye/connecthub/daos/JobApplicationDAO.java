package com.frankaboagye.connecthub.daos;

import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Job;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationDAO {

    private String fullName;
    private String email;
    private String linkedIn;
    private String phoneNumber;
    private String resumeLocation;
    private String coverLetter;

}
