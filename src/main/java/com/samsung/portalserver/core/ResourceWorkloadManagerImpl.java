package com.samsung.portalserver.core;

public class ResourceWorkloadManagerImpl implements WorkloadManager {

    private static final int MIN_STORAGE_SIZE_THRESHOLD = 51200; // MB
    private static final int MAX_CPU_USAGE_THRESHOLD = 90; // %
    private static final int MAX_RAM_USAGE_THRESHOLD = 90; // %

    @Override
    public boolean checkWorkload() {
        ResourceManager resourceManager = new ResourceManager();

        int remainStorageSize = resourceManager.getRemainStorageSize();
        int cpuUsage = resourceManager.getCpuUsage();
        int ramUsage = resourceManager.getRamUsage();

        if(!isEnoughToUseStorage() || !isEnoughToCpuUsage() || !isEnoughToRamUsage()) {
            return false;
        }
        return true;

    }

    private boolean isEnoughToRamUsage() {
        // Check the remain storage size below the threshold

        return true;
    }

    private boolean isEnoughToCpuUsage() {
        // Check the cpu usage exceeds the threshold

        return true;
    }

    private boolean isEnoughToUseStorage() {
        // Check the ram usage exceeds the threshold

        return true;
    }
}
