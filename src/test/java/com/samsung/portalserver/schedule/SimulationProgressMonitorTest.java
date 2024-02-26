package com.samsung.portalserver.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.samsung.portalserver.schedule.job.ScenarioGroupJob;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SimulationProgressMonitorTest {

    SimulationProgressMonitor progressMonitor = new SimulationProgressMonitor();

    @DisplayName("모니터링 목록 추가/삭제 테스트")
    @Test
    void removeJobFromMonitoring() {
        ScenarioGroupJob scenarioGroupJob = new ScenarioGroupJob();

        progressMonitor.addNewJob(scenarioGroupJob);
        assertEquals(1, progressMonitor.count());

        progressMonitor.removeJob(scenarioGroupJob);
        assertEquals(0, progressMonitor.count());
    }
}