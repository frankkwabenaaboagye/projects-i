package com.frankaboagye.connecthub.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ConnectHubConstant {
    CONNECT_HUB_PROFILE("profile"),
    CONNECT_HUB_SESSION_DATA("sessionData"), // this will be the id of the user
    COMPANY_JOBS("companyJobs"),
    COMPANY_PROJECTS("companyProjects"),
    CONNECT_HUB_SKILL("connectHubSkill"),
    CONNECT_HUB_NOTIFICATION("connectHubNotification"),
    CONNECT_HUB_RATING("connectHubRating"),
    CONNECT_HUB_FEEDBACK("connectHubFeedback");

    private final String description;
}
