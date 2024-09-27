package com.frankaboagye.connecthub.interfaces;

import com.frankaboagye.connecthub.entities.Job;

public interface JobServiceInterface {
    Job getJobById(Long Id);
}