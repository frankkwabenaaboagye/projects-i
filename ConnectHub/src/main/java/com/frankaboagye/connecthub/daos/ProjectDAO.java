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
public class ProjectDAO {

    private String title;
    private String description;
    private String deadline;
    private String location;
    private List<String> skills;
    private List<String> otherSkills;
    private String budget;
}
