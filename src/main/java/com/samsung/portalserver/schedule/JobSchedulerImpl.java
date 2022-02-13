package com.samsung.portalserver.schedule;

import com.samsung.portalserver.domain.SimBoard;
import com.samsung.portalserver.repository.SimBoardStatus;
import com.samsung.portalserver.schedule.job.Job;
import com.samsung.portalserver.schedule.job.SimulationJob;
import com.samsung.portalserver.service.SimBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

@Component
public class JobSchedulerImpl implements JobScheduler, Observer {

    private static final Integer EXECUTION_SERVER_IP_TEMP = 99;

    @Autowired
    private SimBoardService simBoardService;
    @Autowired
    private WorkloadManager workloadManager;
    @Autowired
    private ProgressMonitor progressMonitor;

    // DI로 주입하기(wm, pm)
//    public JobSchedulerImpl(WorkloadManager workloadManager, ProgressMonitor progressMonitor) {
//        this.workloadManager = new ResourceWorkloadManagerImpl();
//        this.progressMonitor = new SimulationProgressMonitor();
//        this.progressMonitor.addObserver(this);
//    }

    /**
     * Try scheduling every T seconds
     */
    @Override
    public void tryScheduling() {
        try {
            if (workloadManager.checkPossibleToWork()) {
                Optional<SimBoard> candidate = findNewJob();

                if(candidate.isPresent()) {
                    Job newJob = new SimulationJob(candidate.get());
                    // try scheduling

                    prepare(newJob);

                    executeJob(newJob);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Optional<SimBoard> findNewJob() {
        Optional<SimBoard> newJob = Optional.empty();

        // 저장 프로시저 실행
        long no = simBoardService.findNewSimulation(EXECUTION_SERVER_IP_TEMP);

        newJob = simBoardService.readUniqueRecord(no);
        if (!checkSuccessToFind(newJob)) {
            String errorMsg = String.format(
                    "The execution server of SIM_BOARD(no: %d) and the current server are different.\n" +
                    "Current server is %d, but execution server of SIM_BOARD is %d",
                    no, EXECUTION_SERVER_IP_TEMP, newJob.get().getExecution_server()
            );
            throw new IllegalStateException(errorMsg);
        }
        return newJob;
    }

    private boolean checkSuccessToFind(Optional<SimBoard> newJob) {
        return newJob.isPresent() && newJob.get().getExecution_server() == EXECUTION_SERVER_IP_TEMP;
    }

    @Override
    public void prepare(Job job) {
        SimulationJob simulationJob = (SimulationJob) job;

        // GET Scenario Config
        prepareScenarioConfigFiles(job);

        // set scenario config path
        simulationJob.setConfigDirPath("");
    }

    public void prepareScenarioConfigFiles(Job newJob) {

        if (existInCurrentServer()) {
            // 현재 서버에 있는지 체크

        } else {
            // 현재 서버에 없다면 다른 서버에 요청

            // 정상적으로 받았으면 삭제 요청
        }
        
        moveToConfigDirectory();
    }

    private boolean existInCurrentServer() {
        return false;
    }

    private void moveToConfigDirectory() {
    }

    @Override
    public void executeJob(Job job) {
        SimulationJob simulationJob = (SimulationJob) job;

        Optional<Process> process = runSimulation(simulationJob);
        process.ifPresent(p -> simulationJob.setProcess(p));

        this.progressMonitor.addNewJob(simulationJob);
    }

    private Optional<Process> runSimulation(SimulationJob job) {
        // process 생성하는 부분 팩토리 메서드 패턴으로 수정
        Optional<Process> process = Optional.empty();

        Runtime rt = Runtime.getRuntime();
        try {
            Process p = rt.exec("jps -l");
            process = Optional.ofNullable(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return process;
    }

    @Override
    public void update(Observable o, Object arg) {
        List<SimulationJob> statusChangedJobs = (List<SimulationJob>) arg;
        for (SimulationJob job : statusChangedJobs) {
            // update SIM_BOARD

            if (isTerminated(job)) {
                // 프로시저로 구현
                // delete record from SIM_BOARD
                // add record to SIM_HISTORY

                // remove job from progress monitor
                this.progressMonitor.removeJob(job);
            }
        }

    }

    private boolean isTerminated(SimulationJob job) {
        return job.getStatus().equals(SimBoardStatus.TERMINATED.name());
    }
}
