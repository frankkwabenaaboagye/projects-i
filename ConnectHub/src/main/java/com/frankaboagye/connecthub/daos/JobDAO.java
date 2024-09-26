package com.frankaboagye.connecthub.daos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDAO {

    private String jobTitle;
    private String jobDescription;
    private String salary;
    private String deadline;
    private String location;
}
