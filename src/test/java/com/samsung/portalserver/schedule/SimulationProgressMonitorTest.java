package com.samsung.portalserver.schedule;

import com.samsung.portalserver.schedule.job.SimulationJob;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulationProgressMonitorTest {
    SimulationProgressMonitor progressMonitor = new SimulationProgressMonitor();

    @Test
    void removeJobFromMonitoring() {
        SimulationJob simulationJob = new SimulationJob();

        progressMonitor.addNewJob(simulationJob);
        assertEquals(1, progressMonitor.count());

        progressMonitor.removeJob(simulationJob);
        assertEquals(0, progressMonitor.count());
    }
}