package com.frankaboagye.connecthub.utils;

import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.entities.Job;
import com.frankaboagye.connecthub.enums.Item;
import com.frankaboagye.connecthub.enums.TechnologyInterest;
import com.frankaboagye.connecthub.enums.WorkLabel;
import jakarta.servlet.http.HttpSession;

import java.util.HashSet;
import java.util.Set;

import static com.frankaboagye.connecthub.enums.ConnectHubConstant.PROFILE;
import static com.frankaboagye.connecthub.enums.ConnectHubConstant.SESSION_DATA;
import static com.frankaboagye.connecthub.enums.ConnectHubProfile.COMPANY;

public class CompanyUtils {

    public static void setHttpSession(HttpSession httpSession, Company company){
        httpSession.setAttribute(SESSION_DATA.getDescription(), company.getId());  // e.g. ("sessionData", 29919)
        httpSession.setAttribute(PROFILE.getDescription(), COMPANY.getValue());  // e.g. ("company", company)
    }

    public static void updateTechInterest(Job job, Set<String> selectedItems){
        Set<String> techInterest = job.getTechnologyInterests();
        techInterest.addAll(selectedItems);
        job.setTechnologyInterests(techInterest);
    }

    public static Set<String> getDefaultValueFor(Item item) {
        return switch (item) {
            case TECH_INTEREST -> TechnologyInterest.getAvailableTechnologyInterest();
            case LABELS -> WorkLabel.getSetOfAvailableWorkLabels();
            default -> Set.of("");
        };
    }
}
