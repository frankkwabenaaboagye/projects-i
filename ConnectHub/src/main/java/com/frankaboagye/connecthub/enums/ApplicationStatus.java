package com.frankaboagye.connecthub.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ApplicationStatus {

    SUBMITTED("Submitted by the freelancer"),
    UNDER_REVIEW("Under review by the company"),
    INTERVIEW_SCHEDULED("Interview scheduled"),
    ACCEPTED("Accepted for the project"),
    REJECTED("Rejected by the company"),
    COMPLETED("Project completed"),
    WITHDRAWN("Withdrawn by the freelancer");

    private final String description;

}