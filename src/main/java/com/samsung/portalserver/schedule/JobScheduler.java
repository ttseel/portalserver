package com.samsung.portalserver.schedule;

import com.samsung.portalserver.schedule.job.Job;

public interface JobScheduler {
    void tryScheduling();

    void prepare(Job job);

    void executeJob(Job job);
}
