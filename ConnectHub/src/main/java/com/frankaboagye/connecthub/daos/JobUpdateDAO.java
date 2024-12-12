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
public class JobUpdateDAO {

    private String title;
    private String description;
    private String salary;
    private String deadline;
    private String location;
    private List<String> skills;
    private List<String> otherSkills;
    private String moreInformation;
    private Set<String> responsibilities;
    private Set<String> associatedLabels;
    private Set<String> otherAssociatedLabels;
    private Set<String> technologyInterests;
    private Set<String> otherTechnologyInterests;

    @Override
    public String toString(){
        return "JobUpdateDAO [title=" + title + ", description=" + description + ", salary=" + salary + "]" ;
    }
}
