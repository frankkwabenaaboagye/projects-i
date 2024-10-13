package com.frankaboagye.connecthub.entities;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ConnectHubProfile {
    COMPANY("connect-hub-company"),
    FREELANCER("connect-hub-freelancer"),;


    private final String description;
}
