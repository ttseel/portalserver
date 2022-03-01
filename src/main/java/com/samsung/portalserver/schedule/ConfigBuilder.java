package com.samsung.portalserver.schedule;

import com.samsung.portalserver.schedule.job.Job;

public interface ConfigBuilder {

    void build(Job job) throws Exception;
}
