package com.frankaboagye.connecthub.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The TechnologyInterest enum represents various technology-related skills
 * and areas of expertise that can be relevant to job or project postings or freelancer profiles.
 */
@RequiredArgsConstructor
@Getter
public enum TechnologyInterest {
    JAVA("Java programming language"),
    PYTHON("Python programming language"),
    JS("JavaScript programming language"),
    HTML("Markup language for creating web pages"),
    CSS("Style sheet language for styling web pages"),
    CSHARP("C# programming language"),
    RUBY("Ruby programming language"),
    SWIFT("Programming language for iOS development"),
    KOTLIN("Modern programming language for Android"),
    DB("Databases for data storage"),
    CLOUD("Cloud computing technologies"),
    DATA("Data science and analysis"),
    DEVOPS("Development and operations practices"),
    ML("Machine learning technologies"),
    BLOCKCHAIN("Blockchain technology for secure transactions"),
    SECURITY("Cybersecurity practices and technologies");

    private final String description;

}