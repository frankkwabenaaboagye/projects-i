package com.frankaboagye.connecthub.dtos;//package com.frankaboagye.connecthub.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {
    private String companyName;
    private String companyEmail; // we will not update email
    private String companyPhonenumber;
    private String companyWebsite;
    private String companyDescription;
}
