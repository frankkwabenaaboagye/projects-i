package com.frankaboagye.connecthub.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ApplicationStatus {

    SUBMITTED("Application has been submitted by the freelancer"),
    UNDER_REVIEW("Application is being reviewed by the company"),
    INTERVIEW_SCHEDULED("Interview has been scheduled"),
    ACCEPTED("Application has been accepted for the project"),
    REJECTED("Application has been rejected"),
    COMPLETED("Project completed by the freelancer"),
    WITHDRAWN("Application has been withdrawn by the freelancer");

    private final String description;

}