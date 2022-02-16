package com.samsung.portalserver.schedule;

import com.samsung.portalserver.repository.SimBoardStatus;
import com.samsung.portalserver.schedule.job.Job;
import com.samsung.portalserver.schedule.job.SimulationJob;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class SimulationProgressMonitor extends ProgressMonitor {

    private List<SimulationJob> monitoringList = new ArrayList<>();

    @Override
    public void addNewJob(Job job) {
        SimulationJob simulationJob = (SimulationJob) job;
        monitoringList.add(simulationJob);
    }

    @Override
    public void removeJob(Job job) {
        SimulationJob simulationJob = (SimulationJob) job;
        monitoringList.remove(simulationJob);
    }

    @Override
    public int count() {
        return monitoringList.size();
    }

    @Override
    public void monitoringProgress() {
        List<SimulationJob> statusChangedJobs = new ArrayList<>();
        for (SimulationJob job : monitoringList) {
            boolean isChanged = collectJobStatus(job);

            if (isChanged) {
                statusChangedJobs.add(job);
            }
        }
        notifyChangedStatus(statusChangedJobs);
    }


    public Boolean collectJobStatus(Job job) {
        boolean isChanged = false;

        SimulationJob simulationJob = (SimulationJob) job;

        // replication check

        // status & end_date & termination_reason check
        if (!simulationJob.getProcess().isAlive()){
            // status
            simulationJob.setStatus(SimBoardStatus.TERMINATED.name());
            // end_date

            // termination_reason

            isChanged = true;
        }
        return isChanged;
    }

    private int collectReplication() {

        return 0;
    }

    private LocalDateTime collectEndDate() {

        return null;
    }

    private String collectTerminationReason() {

        return "";
    }

    private boolean isTerminated(SimulationJob job) {
        return job.getStatus().equals(SimBoardStatus.TERMINATED.name());
    }

    private void notifyChangedStatus(List<SimulationJob> statusChangedJobs) {
        notifyObservers(statusChangedJobs);
    }

    private void notifyTerminatedJob(List<SimulationJob> statusChangedJobs) {
        notifyObservers(statusChangedJobs);
    }

}
