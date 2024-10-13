package com.frankaboagye.connecthub.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ConnectHubProfile {
    COMPANY("company"),
    FREELANCER("freelancer"),;


    private final String value;
}
