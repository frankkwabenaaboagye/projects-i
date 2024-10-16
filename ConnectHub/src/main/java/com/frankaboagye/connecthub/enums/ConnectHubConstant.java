package com.frankaboagye.connecthub.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ConnectHubConstant {
    PROFILE("profile"),
    SESSION_DATA("sessionData"), // this will be the id of the user
    JOBS("jobs"),
    PROJECTS("projects");
//    CONNECT_HUB_SKILL("connectHubSkill"),
//    CONNECT_HUB_NOTIFICATION("connectHubNotification"),
//    CONNECT_HUB_RATING("connectHubRating"),
//    CONNECT_HUB_FEEDBACK("connectHubFeedback");

    private final String description;
}
