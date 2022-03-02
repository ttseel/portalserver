package com.samsung.portalserver.schedule;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.samsung.portalserver.common.Subscriber;
import com.samsung.portalserver.schedule.job.Job;
import com.samsung.portalserver.schedule.job.SimulationJob;
import com.samsung.portalserver.schedule.job.SimulationJobList;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SimulationProgressMonitor implements ProgressMonitor {

    private List<Subscriber> subs = new ArrayList<>();
    private List<SimulationJobList> monitoringList = new ArrayList<>();

    @Override
    public void addNewJob(Job job) {
        SimulationJobList simulationJobList = (SimulationJobList) job;
        monitoringList.add(simulationJobList);
    }

    @Override
    public void removeJob(Job job) {
        SimulationJobList simulationJobList = (SimulationJobList) job;
        monitoringList.remove(simulationJobList);
    }

    @Override
    public int count() {
        return monitoringList.size();
    }

    @Override
    @Scheduled(fixedDelay = 10000)
    public void monitoringProgress() {
        System.out.println(String.format("monitoringProgress start / thread name: %s ",
            Thread.currentThread().getName()));

        Map<SimulationJobList, List<SimulationJob>> changedJobs = new ConcurrentHashMap<>();
        for (SimulationJobList job : monitoringList) {
            List<SimulationJob> changedSims = checkSimulationStatus(job);
            if (changedSims.size() > 0) {
                changedJobs.put(job, changedSims);
            }
        }
        notifyChangedStatus(changedJobs);
    }


    public List<SimulationJob> checkSimulationStatus(SimulationJobList job) {
        List<SimulationJob> changedSimulation = new ArrayList<>();

        String fslFilePath = job.getFslFilePath();
        fslFilePath = "/Users/js.oh/Desktop/Developers/simportal/history/USER2/MCPSIM/ScenarioList7/SimulationProgressForSimPortal.log";

        List<String[]> progressLog = readSimulationProgressLog(fslFilePath);
        for (String[] log : progressLog) {
            String[] splitedLog = log[0].split("\t");
            String needCheckLog = splitedLog[splitedLog.length - 1];

            if (needServerCheck(needCheckLog)) {
                String scenario = splitedLog[0];
                SimulationJob simulationJob = job.getSimulationMap().get(scenario);

                boolean isChanged = compareWithLog(splitedLog, simulationJob);
                if (isChanged) {
                    changedSimulation.add(simulationJob);
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

    private boolean compareWithLog(String[] splitedLog, SimulationJob simulationJob) {
        boolean isCheanged = false;
        String repLog = splitedLog[2];
        String terminationReasonLog = splitedLog[4];
        String endDateLog = splitedLog[5];

        if (simulationJob.getCurrent_rep() != Integer.parseInt(repLog)) {
            simulationJob.setCurrent_rep(Integer.parseInt(repLog));
            isCheanged = true;
        }
        if (simulationJob.getEnd_date() != LocalDateTime.parse(endDateLog)) {
            simulationJob.setEnd_date(LocalDateTime.parse(endDateLog));
            isCheanged = true;
        }
        if (!simulationJob.getTermination_reason().equals(terminationReasonLog)) {
            simulationJob.setTermination_reason(terminationReasonLog);
            isCheanged = true;
        }

        return isCheanged;
    }

    private void notifyChangedStatus(
        Map<SimulationJobList, List<SimulationJob>> statusChangedJobs) {
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
