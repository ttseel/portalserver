package com.samsung.portalserver.schedule;

import com.samsung.portalserver.schedule.job.Job;

import java.util.Observable;

public abstract class ProgressMonitor extends Observable {

    public abstract void addNewJob(Job job);

    public abstract void removeJob(Job job);

    public abstract int count();

    public abstract void monitoringProgress();

}
