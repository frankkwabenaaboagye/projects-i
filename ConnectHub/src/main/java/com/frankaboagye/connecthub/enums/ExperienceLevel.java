package com.frankaboagye.connecthub.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The ExperienceLevel enum represents different levels of professional experience.
 * It can be used across various fields and sectors to classify candidates or job postings.
 */
@RequiredArgsConstructor
@Getter
public enum ExperienceLevel {
    ENTRY_LEVEL("Suitable for recent graduates or those with minimal work experience."),
    JUNIOR("For individuals with some experience, typically 1-3 years."),
    MID_LEVEL("For professionals with a moderate level of experience, usually 3-5 years."),
    SENIOR("For highly experienced professionals, typically 5-10 years."),
    LEAD("For those in leadership positions, overseeing projects or teams."),
    EXPERT("For individuals recognized as experts in their field, with extensive experience."),
    EXECUTIVE("For high-level management positions, such as C-level roles.");

    // create a separate customer info page where detaisl fo these will be availale

    private final String description;
}
