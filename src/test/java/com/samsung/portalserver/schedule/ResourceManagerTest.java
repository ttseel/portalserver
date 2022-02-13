package com.samsung.portalserver.schedule;

import org.junit.jupiter.api.Test;

class ResourceManagerTest {
    ResourceManager rm = new ResourceManager();

    @Test
    void getRemainStorageSize() {
        System.out.println(rm.getUsableStorageSize("/"));
    }

    @Test
    void getRemainStorageRatio() {
        System.out.println(rm.getUsableStorageRatio("/"));
    }

    @Test
    void getCpuUsage() {
        System.out.println(rm.getCpuUsage());
    }

    @Test
    void getMemoryUsage() {
        System.out.println(rm.getMemoryUsage());
    }
}