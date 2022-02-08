package com.samsung.portalserver.core;

public class JobSchedulerImpl implements JobScheduler {

    WorkloadManager workloadManager;

    // DI로 주입하기
    public JobSchedulerImpl(WorkloadManager workloadManager) {
//        this.resourceManager = resourceManager;
        this.workloadManager = new ResourceWorkloadManagerImpl();
    }

    /**
     * Try scheduling every T seconds
     */
    public void tryScheduleing() {
        if (workloadManager.checkWorkload()) {
            // try scheduling
        } else {
            // try after T seconds

            return;
        }

        // execute simulation
    }

}
