package com.samsung.portalserver.simulation;

import static com.samsung.portalserver.simulation.FileConstants.SIMULATOR_DIR_PATH;

import com.samsung.portalserver.exceptions.GroupLevelException;
import com.samsung.portalserver.schedule.job.SimulationJobList;
import java.io.IOException;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ExternalJarAbstractSimulatorFactory extends AbstractSimulatorFactory {

    @Override
    public Optional<Process> create(SimulationJobList job) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process process;

        String simualtorPath = String.format("%s/%s/%s/%s_%s.jar", SIMULATOR_DIR_PATH,
            job.getSimulator(), job.getVersion(), job.getSimulator(), job.getVersion());

        String executeCmd = String.format("java -jar %s ", simualtorPath);

        String args = "";
        switch (SimulatorCategory.getCategoryByString(job.getSimulator())) {
            case MCPSIM:
                String configPath = String.format("%s ", job.getConfigDirPath());
                String logPath = String.format("%s ", job.getGroupDirPath());
                process = rt.exec(executeCmd + configPath + logPath);
                break;
            case OCS3SIM:
                args = String.format("%s %s", job.getSimulator(), job.getVersion());
                process = rt.exec(executeCmd + args);
                break;
            case OCS4SIM:
                args = String.format("%s %s", job.getSimulator(), job.getVersion());
                process = rt.exec(executeCmd + args);
                break;
            case SeeFlow:
                args = String.format("%s %s", job.getSimulator(), job.getVersion());
                process = rt.exec(executeCmd + args);
                break;
            case REMOTE_SIM:
                args = String.format("%s %s", job.getSimulator(), job.getVersion());
                process = rt.exec(executeCmd + args);
                break;
            default:
                process = null;
                break;
        }
        if (process == null) {
            throw new GroupLevelException();
        }

        return Optional.ofNullable(process);
    }
}