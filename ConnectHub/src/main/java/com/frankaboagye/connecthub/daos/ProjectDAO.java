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
public class ProjectDAO {

    private String title;
    private String description;
    private String budget;
    private String deadline;
    private String location;
    private Set<String> skills;
    private Set<String> otherSkills;
    private Set<String> experiences;
    private Set<String> otherExperiences;

}
