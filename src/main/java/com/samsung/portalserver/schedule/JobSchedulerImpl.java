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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

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

    /**
     * Try scheduling every T seconds
     */
    @Override
    @Scheduled(fixedDelay = 6000)
    public void tryScheduling() {
        System.out.println("tryScheduling..");
        try {
            if (workloadManager.checkPossibleToWork()) {
                Optional<SimBoard> candidate = findNewJob();

                if(candidate.isPresent()) {
                    SimulationJob simulationJob = new SimulationJob(candidate.get());

                    // try scheduling
                    prepare(simulationJob);
                    executeJob(simulationJob);
                    this.progressMonitor.addNewJob(simulationJob);
                } else {
                    System.out.println("SIM_BOARD does not have RESERVED Job");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            simBoardService.updateStatus(Long.valueOf(e.getCause().getMessage()), SimBoardStatus.ERROR.name());
        } finally {

        }
    }

    private Optional<SimBoard> findNewJob() {
        Optional<SimBoard> newJob = Optional.empty();

        // 저장 프로시저 실행
        long no = simBoardService.findNewSimulation(CURRENT_SERVER_TEMP);
        newJob = simBoardService.readUniqueRecord(no);

        checkSuccessToFind(newJob);

        return newJob;
    }

    private void checkSuccessToFind(Optional<SimBoard> newJob) {
        // SIM_BOARD의 execution_server와 현재 서버의 번호가 일치하는지 확인
        newJob.ifPresent(simBoard -> checkServerMatching(simBoard.getNo(), simBoard.getExecution_server()));
    }

    private void checkServerMatching(Long no, Integer executionServer) {
        if (!Objects.equals(executionServer, CURRENT_SERVER_TEMP)) {
            String errorMsg = String.format(
                    "The execution server of SIM_BOARD(no: %d) and the current server are different. " +
                    "Current server is %d, but database value is %d",
                    no, CURRENT_SERVER_TEMP, executionServer
            );
            throw new IllegalStateException(errorMsg, new Throwable(String.valueOf(no)));
        }
    }

    @Override
    public void prepare(Job job) {
        SimulationJob simulationJob = (SimulationJob) job;
        try {
            String configPath = FileService.HISTORY_DIR_PATH
                    + FileService.DIR_DELIMETER + simulationJob.getUser()
                    + FileService.DIR_DELIMETER + simulationJob.getSimulator()
                    + FileService.DIR_DELIMETER + simulationJob.getFslName()
                    + FileService.DIR_DELIMETER + FileService.CONFIG_DIR_NAME;

            // Set scenario config path
            simulationJob.setConfigDirPath(configPath);

            // Prepare Scenario Config
            prepareScenarioConfigFiles(simulationJob);
        } catch (Exception e) {
            // prepare 과정에서 문제가 발생하면 SIM_BOARD status를 ERROR로 변경
            String errorMsg = String.format("Exception occured in prepare");
            throw new IllegalStateException(errorMsg, new Throwable(String.valueOf(simulationJob.getSimBoardPKNo())));
        }
    }

    public void prepareScenarioConfigFiles(SimulationJob job) {
        if (!existInCurrentServer(job)) {
            // 현재 서버에 없다면 다른 서버에 요청(요청 받는 서버에서 삭제까지 수행)
            System.out.println(String.format("Current Server(no: %d) does not have a Scenario config files.", CURRENT_SERVER_TEMP));
            System.out.println(String.format("Request config files from Server: %d", job.getExecution_server()));
//            requestSimConfigFiles(job);
//            moveToConfigDirectory(job);
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
    public void executeJob(Job job) throws IOException {
        SimulationJob simulationJob = (SimulationJob) job;

        Optional<Process> process = SimulationProcessFactory(simulationJob);
        process.ifPresent(p -> simulationJob.setProcess(p));
    }


    private Optional<Process> SimulationProcessFactory(SimulationJob job) throws IOException {
        // 팩토리 메서드 패턴으로 수정하기 !!
        Runtime rt = Runtime.getRuntime();
        Process process = null;

        String executeCmd = String.format("java -jar %s/%s/%s/%s_%s.jar ", FileService.SIMULATOR_DIR_PATH, job.getSimulator(), job.getVersion(), job.getSimulator(), job.getVersion());
        String args = "";
        try {
            switch (SimulatorCategory.getCategoryByString(job.getSimulator())) {
                case MCPSIM:
                    args = String.format("%s %s",job.getSimulator(), job.getVersion());
                    process = rt.exec(executeCmd + args);
                    break;
                case OCS3SIM:
                    args = String.format("%s %s",job.getSimulator(), job.getVersion());
                    process = rt.exec(executeCmd + args);
                    break;
                case OCS4SIM:
                    args = String.format("%s %s",job.getSimulator(), job.getVersion());
                    process = rt.exec(executeCmd + args);
                    break;
                case SeeFlow:
                    args = String.format("%s %s",job.getSimulator(), job.getVersion());
                    process = rt.exec(executeCmd + args);
                    break;
                case REMOTE_SIM:
                    args = String.format("%s %s",job.getSimulator(), job.getVersion());
                    process = rt.exec(executeCmd + args);
                    break;
                default:
                    process = null;
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage(), new Throwable(String.valueOf(job.getSimBoardPKNo())));
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
        return job.getStatus().equals(SimBoardStatus.ERROR.name());
    }
}
