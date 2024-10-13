package com.frankaboagye.connecthub.daos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectApplicationDAO {

    private String fullName;
    private String email;
    private String c;
    private String skills;
    private String deadline;
    private String documentName;
    private String location;

}
