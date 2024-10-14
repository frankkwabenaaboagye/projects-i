//package com.frankaboagye.connecthub.enums;
//
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * The GeneralSkills enum represents various general skills that companies may require
// * and freelancers may possess. This enum can be utilized to categorize skills
// * across multiple domains and job types.
// */
//@RequiredArgsConstructor
//@Getter
//public enum GeneralSkills {
//    COMMUNICATION("Communication"),
//    TEAMWORK("Teamwork"),
//    PROBLEM_SOLVING("Problem Solving"),
//    TIME_MANAGEMENT("Time Management"),
//    ADAPTABILITY("Adaptability"),
//    TECHNICAL_PROFICIENCY("Technical Proficiency"),
//    PROJECT_MANAGEMENT("Project Management"),
//    LEADERSHIP("Leadership"),
//    ANALYTICAL_THINKING("Analytical Thinking"),
//    CREATIVITY("Creativity"),
//    ATTENTION_TO_DETAIL("Attention to Detail"),
//    CUSTOMER_SERVICE("Customer Service"),
//    RESEARCH("Research"),
//    WRITING("Writing"),
//    MARKETING("Marketing"),
//    PROGRAMMING("Programming"),
//    DATA_ANALYSIS("Data Analysis"),
//    DESIGN("Design"),
//    SALES("Sales"),
//    NETWORKING("Networking"),
//    STRATEGIC_PLANNING("Strategic Planning"),
//    CONFLICT_RESOLUTION("Conflict Resolution"),
//    NEGOTIATION("Negotiation"),
//    PRESENTATION("Presentation"),
//    SOCIAL_MEDIA_MANAGEMENT("Social Media Management"),
//    DIGITAL_MARKETING("Digital Marketing"),
//    SOFTWARE_DEVELOPMENT("Software Development"),
//    UX_UI_DESIGN("UX/UI Design"),
//    CONTENT_CREATION("Content Creation"),
//    DATABASE_MANAGEMENT("Database Management"),
//    CLOUD_COMPUTING("Cloud Computing"),
//    MACHINE_LEARNING("Machine Learning"),
//    CYBERSECURITY("Cybersecurity"),
//    BUSINESS_ANALYSIS("Business Analysis"),
//    FINANCIAL_ANALYSIS("Financial Analysis"),
//    EDUCATIONAL_TECHNOLOGY("Educational Technology"),
//    ECOMMERCE("Ecommerce"),
//    SEO("SEO"),
//    QUALITY_ASSURANCE("Quality Assurance"),
//    EVENT_PLANNING("Event Planning"),
//    BRAND_MANAGEMENT("Brand Management"),
//    LOGISTICS("Logistics"),
//    FACILITATION("Facilitation");
//
//    private final String simpleName;
//
//
//    public static List<String> getAvailableSkills() {
//        return Arrays.stream(GeneralSkills.values())
//                .map(GeneralSkills::getSimpleName) // Use simple name directly
//                .collect(Collectors.toList()); // Collect to a List<String>
//    }
//}