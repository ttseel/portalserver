package com.samsung.portalserver.schedule;

import com.samsung.portalserver.common.Subscribable;
import com.samsung.portalserver.schedule.job.Job;

public interface ProgressMonitor extends Subscribable {

    public abstract void addNewJob(Job job);

    public abstract void removeJob(Job job);

    public abstract int count();

    public abstract void monitoringProgress();
}
