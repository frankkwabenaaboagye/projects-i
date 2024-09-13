package com.frankaboagye.connecthub.daos;

import lombok.*;


@Data  // getter, setter, toString, equals, hashcode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDAO {

    private String companyName;
    private String companyEmail;
    private String companyPhoneNumber;
    private String companyWebsite;
    private String companyPassword;
    private String companyConfirmPassword;
}
