package com.frankaboagye.connecthub.daos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyUpdateDAO {
    private String name;
    private String email; // we will not update email
    private String phonenumber;
    private String website;
    private String description;
    private String address;
}
