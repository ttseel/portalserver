package com.samsung.portalserver.schedule;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class ProcessManagerTest {

    @Test
    void findProcessIdByProcess() {
    }

    @Test
    void findProcessIdByName() {
        String processName = "sun.tools.jps.Jps";
        Map<Integer, Boolean> processIds = ProcessManager.findProcessIdByName(processName);

        if (processIds.size() != 1)
            throw new IllegalStateException();
    }
}