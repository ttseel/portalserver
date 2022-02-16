package com.samsung.portalserver.schedule;

import com.samsung.portalserver.domain.SimBoard;
import com.samsung.portalserver.domain.SimulatorCategory;
import com.samsung.portalserver.repository.SimBoardStatus;
import com.samsung.portalserver.schedule.job.Job;
import com.samsung.portalserver.schedule.job.SimulationJob;
import com.samsung.portalserver.service.FileService;
import com.samsung.portalserver.service.SimBoardService;
import com.samsung.portalserver.service.SimHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static com.samsung.portalserver.domain.SimulatorCategory.MCPSIM;

@Component
public class JobSchedulerImpl implements JobScheduler, Observer {

    public static final Integer CURRENT_SERVER_TEMP = 99;

    @Autowired
    private SimBoardService simBoardService;
    @Autowired
    private SimHistoryService simHistoryService;
    @Autowired
    private WorkloadManager workloadManager;
    @Autowired
    private ProgressMonitor progressMonitor;
    private FileService fileService = new FileService();


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
                    this.progressMonitor.addNewJob(newJob);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Optional<SimBoard> findNewJob() {
        Optional<SimBoard> newJob = Optional.empty();

        // 저장 프로시저 실행
        long no = simBoardService.findNewSimulation(CURRENT_SERVER_TEMP);

        newJob = simBoardService.readUniqueRecord(no);
        if (!checkSuccessToFind(newJob)) {
            String errorMsg = String.format(
                    "The execution server of SIM_BOARD(no: %d) and the current server are different.\n" +
                    "Current server is %d, but execution server of SIM_BOARD is %d",
                    no, CURRENT_SERVER_TEMP, newJob.get().getExecution_server()
            );
            throw new IllegalStateException(errorMsg);
        }
        return newJob;
    }

    private boolean checkSuccessToFind(Optional<SimBoard> newJob) {
        return newJob.isPresent() && Objects.equals(newJob.get().getExecution_server(), CURRENT_SERVER_TEMP);
    }

    @Override
    public void prepare(Job job) {
        SimulationJob simulationJob = (SimulationJob) job;

        String configPath = FileService.HISTORY_DIR_PATH
                + FileService.DIR_DELIMETER + simulationJob.getUser()
                + FileService.DIR_DELIMETER + simulationJob.getSimulator()
                + FileService.DIR_DELIMETER + simulationJob.getFslName()
                + FileService.DIR_DELIMETER + FileService.CONFIG_DIR_NAME;

        // Set scenario config path
        simulationJob.setConfigDirPath(configPath);

        // Prepare Scenario Config
        prepareScenarioConfigFiles(simulationJob);
    }

    public void prepareScenarioConfigFiles(SimulationJob job) {
        if (!existInCurrentServer(job)) {
            // 현재 서버에 없다면 다른 서버에 요청(요청 받는 서버에서 삭제까지 수행)
            requestSimConfigFiles(job);
            moveToConfigDirectory(job);
        }
        setFslAndFssFileName(job);
    }

    private boolean existInCurrentServer(SimulationJob job) {
        return Objects.equals(job.getReservation_server(), CURRENT_SERVER_TEMP);
    }

    private void setFslAndFssFileName(SimulationJob job) {
        List<String> fileNameList = fileService.getFileList(job.getConfigDirPath());
        fileNameList.forEach(fileName -> {
            String[] splited = fileName.split("\\.");
            String extension = splited[splited.length-1];
            if (extension.equals( "fsl")) {
                job.setFslFilePath(job.getConfigDirPath() + FileService.DIR_DELIMETER + fileName);
            } else if (extension.equals("fss")) {
                job.getFssFilePath().add(job.getConfigDirPath() + FileService.DIR_DELIMETER + fileName);
            }
        });
    }

    private void requestSimConfigFiles(SimulationJob job) {

    }

    private void moveToConfigDirectory(SimulationJob job) {
        fileService.createDirectories(job.getConfigDirPath());
//        job.setConfigDirPath();
    }

    @Override
    public void executeJob(Job job) {
        SimulationJob simulationJob = (SimulationJob) job;

        Optional<Process> process = SimulationProcessFactory(simulationJob);
        process.ifPresent(p -> simulationJob.setProcess(p));
    }

    // process 생성하는 부분 팩토리 메서드 패턴으로 수정
    private Optional<Process> SimulationProcessFactory(SimulationJob job) {
        Runtime rt = Runtime.getRuntime();
        Process process = null;

        String executeCmd = String.format("java -jar %s/%s_%s.jar", FileService.SIMULATOR_DIR_PATH, job.getSimulator(), job.getVersion());
        String args = "";
        try {
            switch (SimulatorCategory.getCategoryByString(job.getSimulator())) {
                case MCPSIM:
                    args = "";
                    process = rt.exec(executeCmd + args);
                    break;
                case OCS3SIM:
                    args = "";
                    process = rt.exec(executeCmd + args);
                    break;
                case OCS4SIM:
                    args = "";
                    process = rt.exec(executeCmd + args);
                    break;
                case SeeFlow:
                    args = "";
                    process = rt.exec(executeCmd + args);
                    break;
                case REMOTE_SIM:
                    args = "";
                    process = rt.exec(executeCmd + args);
                    break;
                default:
                    process = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(process);
    }

    @Override
    public void update(Observable o, Object arg) {
        List<SimulationJob> statusChangedJobs = (List<SimulationJob>) arg;
        for (SimulationJob job : statusChangedJobs) {
            if (isTerminated(job)) {
                // procedure: delete record from SIM_BOARD, add record to SIM_HISTORY
                simHistoryService.moveFromBoardToHistory(job);
                // remove job from progress monitor
                progressMonitor.removeJob(job);
            } else {
                // update SIM_BOARD: current_rep
                simBoardService.updateCurrentRep(job.getSimBoardPKNo(), job.getCurrent_rep());
            }
        }
    }

    private boolean isTerminated(SimulationJob job) {
        return job.getStatus().equals(SimBoardStatus.TERMINATED.name());
    }
}
