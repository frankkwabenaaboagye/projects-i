package com.frankaboagye.connecthub.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum WorkLabel {

    FULL_TIME("Full-time employment with regular hours"),
    PART_TIME("Part-time employment with reduced hours"),
    INTERNSHIP("Short-term work for gaining experience, typically for students"),
    APPRENTICE("On-the-job training while learning a trade or skill"),
    TEMPORARY("Employment for a fixed period of time"),
    CONTRACT("Work on a contractual basis for a set duration or project"),
    REMOTE("Work that can be done from any location"),
    TEAM("Collaborative work with a group of people"),
    INDIVIDUAL("Work that is done independently"),
    FREELANCE("Self-employed work, often on a per-project basis"),
    ON_SITE("Work that requires physical presence at a specific location"),
    CONSULTANCY("Advisory or professional service provided for a fee"),
    SEASONAL("Work that is available during specific times of the year"),
    VOLUNTEER("Unpaid work done for charitable or community reasons"),
    PROJECT_BASED("Work that revolves around the completion of a specific project"),
    SHIFT_BASED("Work done in rotating shifts, typically in 24/7 operations");

    private final String description;

    public static List<String> getAvailableWorkLabels() {
        return Arrays.stream(WorkLabel.values())
                .map(worklabel -> worklabel.name().replace("_", " "))
                .collect(Collectors.toList()); // Collect to a List<String>
    }
}