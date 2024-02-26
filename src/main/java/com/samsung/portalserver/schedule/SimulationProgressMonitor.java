package com.samsung.portalserver.schedule;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.samsung.portalserver.api.dto.UniqueSimulationRecordDto;
import com.samsung.portalserver.common.Subscriber;
import com.samsung.portalserver.repository.SimBoardStatus;
import com.samsung.portalserver.schedule.job.Job;
import com.samsung.portalserver.schedule.job.ScenarioJob;
import com.samsung.portalserver.schedule.job.ScenarioGroupJob;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class SimulationProgressMonitor implements ProgressMonitor {

    private List<Subscriber> subs = new ArrayList<>();
    private List<ScenarioGroupJob> monitoringList = new ArrayList<>();

    @Override
    public void addNewJob(Job job) {
        ScenarioGroupJob scenarioGroupJob = (ScenarioGroupJob) job;
        monitoringList.add(scenarioGroupJob);
    }

    @Override
    public void removeJob(Job job) {
        ScenarioGroupJob scenarioGroupJob = (ScenarioGroupJob) job;
        monitoringList.remove(scenarioGroupJob);
    }

    @Override
    public int count() {
        return monitoringList.size();
    }

    @Override
//    @Scheduled(fixedDelay = 10000)
    public void monitoringProgress() {
        System.out.println(String.format("monitoringProgress start / thread name: %s ",
            Thread.currentThread().getName()));

        Map<ScenarioGroupJob, List<ScenarioJob>> changedJobs = new ConcurrentHashMap<>();
        for (ScenarioGroupJob job : monitoringList) {
            List<ScenarioJob> changedSims = checkSimulationStatus(job);
            if (changedSims.size() > 0) {
                changedJobs.put(job, changedSims);
            }
        }
        notifyChangedStatus(changedJobs);
    }


    public List<ScenarioJob> checkSimulationStatus(ScenarioGroupJob job) {
        List<ScenarioJob> changedSimulation = new ArrayList<>();

        String fslFilePath = job.getFslFilePath();
        fslFilePath = "/Users/js.oh/Desktop/Developers/simportal/history/USER2/MCPSIM/ScenarioList7/SimulationProgressForSimPortal.log";

        List<String[]> progressLog = readSimulationProgressLog(fslFilePath);
        for (String[] log : progressLog) {
            String[] splitedLog = log[0].split("\t");
            String needCheckLog = splitedLog[splitedLog.length - 1];

            if (needServerCheck(needCheckLog)) {
                String scenario = splitedLog[0];
                ScenarioJob scenarioJob = job.getScenarioMap().get(scenario);

                boolean isChanged = compareWithLog(splitedLog, scenarioJob);
                if (isChanged) {
                    changedSimulation.add(scenarioJob);
                }
            }
        }

        return changedSimulation;
    }

    private List<String[]> readSimulationProgressLog(String fslFilePath) {
        List<String[]> logLines = null;
        try {
            CSVReader reader = new CSVReader(
                new InputStreamReader(new FileInputStream(fslFilePath)));
            logLines = reader.readAll();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return logLines;
    }


    private boolean needServerCheck(String serverCheck) {
        return serverCheck.equals("NO");
    }

    private boolean compareWithLog(String[] splitedLog, ScenarioJob scenarioJob) {
        boolean isCheanged = false;
        String repLog = splitedLog[2];
        String terminationReasonLog = splitedLog[4];
        String endDateLog = splitedLog[5];

        if (scenarioJob.getCurrent_rep() != Integer.parseInt(repLog)) {
            scenarioJob.setCurrent_rep(Integer.parseInt(repLog));
            isCheanged = true;
        }
        if (scenarioJob.getStatus().equals(SimBoardStatus.WAITING.name())) {
            scenarioJob.setStatus(SimBoardStatus.RUNNING.name());
            isCheanged = true;
        }
        if (scenarioJob.getEnd_date() != LocalDateTime.parse(endDateLog)) {
            scenarioJob.setEnd_date(LocalDateTime.parse(endDateLog));
            isCheanged = true;
        }
        if (!scenarioJob.getTermination_reason().equals(terminationReasonLog)) {
            scenarioJob.setTermination_reason(terminationReasonLog);
            isCheanged = true;
        }

        return isCheanged;
    }

    public boolean stopSimulation(UniqueSimulationRecordDto dto) {
        boolean isStopped = false;
        Optional<ScenarioGroupJob> simulationJobList = findStopGroup(dto);
        if (simulationJobList.isPresent()) {

            ScenarioGroupJob removeTarget = simulationJobList.get();
            removeTarget.getProcess().destroy();

            if (!removeTarget.getProcess().isAlive()) {
                monitoringList.remove(removeTarget);
                isStopped = true;
            }
        }

        return isStopped;
    }

    private Optional<ScenarioGroupJob> findStopGroup(UniqueSimulationRecordDto dto) {
        for (ScenarioGroupJob scenarioGroupJob : monitoringList) {
            if (isStopGroup(scenarioGroupJob, dto)) {
                return Optional.of(scenarioGroupJob);
            }
        }
        return Optional.empty();
    }

    private boolean isStopGroup(ScenarioGroupJob jobList, UniqueSimulationRecordDto dto) {
        if (jobList.getFslName().equals(dto.getGroup()) && jobList.getUser().equals(dto.getUser())
            && jobList.getSimulator().equals(dto.getSimulator())) {
            return true;
        }
        return false;
    }

    private void notifyChangedStatus(Map<ScenarioGroupJob, List<ScenarioJob>> statusChangedJobs) {
        notifytSubscribers(statusChangedJobs);
    }

    @Override
    public void addSubscriber(Subscriber s) {
        if (s == null) {
            throw new NullPointerException();
        }
        if (!subs.contains(s)) {
            subs.add(s);
        }
    }

    @Override
    public void notifytSubscribers() {
        notifytSubscribers(null);
    }

    @Override
    public void notifytSubscribers(Object arg) {
        subs.forEach(subscriber -> subscriber.update(this, arg));
    }

    @Override
    public int countSubscribers() {
        return subs.size();
    }
}
