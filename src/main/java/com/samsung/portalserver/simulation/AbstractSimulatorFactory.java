package com.samsung.portalserver.simulation;

import com.samsung.portalserver.schedule.job.SimulationJobList;
import java.io.IOException;
import java.util.Optional;

public abstract class AbstractSimulatorFactory {

    public abstract Optional<Process> create(SimulationJobList job) throws IOException;
}
