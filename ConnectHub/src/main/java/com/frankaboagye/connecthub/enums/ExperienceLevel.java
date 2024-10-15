package com.frankaboagye.connecthub.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The ExperienceLevel enum represents different levels of professional experience.
 * It can be used across various fields and sectors to classify candidates or job postings.
 */
@RequiredArgsConstructor
@Getter
public enum ExperienceLevel {
    ENTRY_LEVEL("Entry Level"),
    JUNIOR("Junior"),
    MID_LEVEL("Mid Level"),
    SENIOR("Senior"),
    LEAD("Lead"),
    EXPERT("Expert"),
    EXECUTIVE("Executive");

    // create a separate customer info page where detaisl fo these will be availale

    public static List<String> getAvailableSkills() {
        return Arrays.stream(ExperienceLevel.values())
                .map(ExperienceLevel::getDescription) // Use simple name directly
                .collect(Collectors.toList()); // Collect to a List<String>
    }

    private final String description;
}
