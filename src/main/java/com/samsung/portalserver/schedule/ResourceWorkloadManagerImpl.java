package com.samsung.portalserver.schedule;

import org.springframework.stereotype.Component;

@Component
public class ResourceWorkloadManagerImpl implements WorkloadManager {

    private static final int MIN_STORAGE_SIZE_THRESHOLD = 50; // MB
    private static final int MAX_CPU_USAGE_THRESHOLD = 90; // %
    private static final int MAX_RAM_USAGE_THRESHOLD = 90; // %

    private static final ResourceManager resourceManager = new ResourceManager();

    @Override
    public boolean checkPossibleToWork() {
//        if(!isEnoughToUseStorage() || !isEnoughToCpuUsage() || !isEnoughToMemoryUsage()) {
//            return false;
//        }
        return true;
    }

    private boolean isEnoughToUseStorage() {
        // Check the ram usage exceeds the threshold
        return MIN_STORAGE_SIZE_THRESHOLD < resourceManager.getUsableStorageSize("/");
    }


    private boolean isEnoughToCpuUsage() {
        // Check the cpu usage exceeds the threshold
        return MAX_CPU_USAGE_THRESHOLD > resourceManager.getCpuUsage();
    }

    private boolean isEnoughToMemoryUsage() {
        // Check the remain storage size below the threshold
        return MAX_RAM_USAGE_THRESHOLD > resourceManager.getMemoryUsage();
    }
}
