package com.frankaboagye.connecthub.daos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateDAO {

    private String title;
    private String description;
    private String deadline;
    private String location;
    private List<String> skills;
    private List<String> experiences;
    private String budget;
}
