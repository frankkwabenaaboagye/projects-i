package com.frankaboagye.connecthub.daos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDAO {

    private String title;
    private String description;
    private String companyId;
    private String skills;
    private String deadline;
    private String documentName;
    private String location;
}
