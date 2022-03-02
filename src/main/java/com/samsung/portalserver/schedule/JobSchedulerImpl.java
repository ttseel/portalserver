package com.samsung.portalserver.schedule;

import com.samsung.portalserver.common.Subscribable;
import com.samsung.portalserver.common.Subscriber;
import com.samsung.portalserver.domain.SimBoard;
import com.samsung.portalserver.domain.SimulatorCategory;
import com.samsung.portalserver.repository.SimBoardStatus;
import com.samsung.portalserver.schedule.job.Job;
import com.samsung.portalserver.schedule.job.NewSimulationJobDto;
import com.samsung.portalserver.schedule.job.SimulationJob;
import com.samsung.portalserver.schedule.job.SimulationJobList;
import com.samsung.portalserver.service.FileService;
import com.samsung.portalserver.service.SimBoardService;
import com.samsung.portalserver.service.SimHistoryService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobSchedulerImpl implements JobScheduler, Subscriber {

    public static final Integer CURRENT_SERVER_TEMP = 99;

    @Autowired
    private SimBoardService simBoardService;
    @Autowired
    private SimHistoryService simHistoryService;
    @Autowired
    private WorkloadManager workloadManager;
    private ProgressMonitor progressMonitor;
    private FileService fileService = new FileService();

    @Autowired
    public JobSchedulerImpl(ProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
        this.progressMonitor.addSubscriber(this);
    }

    /**
     * Try scheduling every T seconds
     */
    @Override
    @Scheduled(fixedDelay = 10000)
    public void tryScheduling() {
        System.out.println(String.format("tryScheduling start / thread name: %s ",
            Thread.currentThread().getName()));

        try {
            if (workloadManager.checkPossibleToWork()) {
                Optional<List<SimBoard>> newJobs = findNewJob();

                if (newJobs.isPresent()) {
                    SimulationJobList simulationJobList = new SimulationJobList(
                        newJobs.get().get(0));
                    newJobs.get().forEach(simBoard -> {
                        simulationJobList.getSimulationMap()
                            .put(simBoard.getScenario(), new SimulationJob(simBoard));
                    });

                    // try scheduling
                    prepare(simulationJobList);
//                    executeJob(simulationJobList);
                    this.progressMonitor.addNewJob(simulationJobList);
                } else {
                    System.out.println("SIM_BOARD does not have RESERVED Job");
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
//            simBoardService.updateStatus(Long.valueOf(e.getCause().getMessage()),
//                SimBoardStatus.ERROR.name());
        }
    }

    private Optional<List<SimBoard>> findNewJob() {
        Optional<List<SimBoard>> newSimulationJobs;

        // 저장 프로시저 실행
        NewSimulationJobDto newSimJobDto = simBoardService.findNewSimulation(CURRENT_SERVER_TEMP);
        newSimulationJobs = simBoardService.readUniqueFsl(newSimJobDto.getFslName(),
            newSimJobDto.getUser(), newSimJobDto.getSimulator());

        newSimulationJobs.ifPresent(simBoards -> checkSuccessToFind(newSimulationJobs.get()));

        return newSimulationJobs;
    }

    private void checkSuccessToFind(List<SimBoard> newJob) {
        // SIM_BOARD의 execution_server와 현재 서버의 번호가 일치하는지, Status가 Waiting인지 확인
        newJob.forEach(simBoard -> {
            checkServerMatching(simBoard);
            checkStatusIsWaiting(simBoard);
        });
    }

    private void checkServerMatching(SimBoard simBoard) {
        if (!Objects.equals(simBoard.getExecution_server(), CURRENT_SERVER_TEMP)) {
            String errorMsg = String.format(
                "The execution server of SIM_BOARD(no: %d) and the current server are different. "
                    + "Current server is %d, but database value is %d", simBoard.getNo(),
                CURRENT_SERVER_TEMP, simBoard.getExecution_server());
            throw new IllegalStateException(errorMsg,
                new Throwable(String.valueOf(simBoard.getNo())));
        }
    }

    private void checkStatusIsWaiting(SimBoard simBoard) {
        if (!Objects.equals(simBoard.getStatus(), SimBoardStatus.WAITING.name())) {
            String errorMsg = String.format(
                "The status of SIM_BOARD(no: %d) is not Waiting. database value is %s",
                simBoard.getNo(), simBoard.getStatus());
            throw new IllegalStateException(errorMsg,
                new Throwable(String.valueOf(simBoard.getNo())));
        }
    }

    @Override
    public void prepare(Job job) {
        SimulationJobList simulationJobList = (SimulationJobList) job;
        Optional<ConfigBuilder> configBuilder = setConfigBuilder(
            SimulatorCategory.getCategoryByString(simulationJobList.getSimulator()));
        try {
            if (configBuilder.isPresent()) {
                prepareScenarioConfigFiles(simulationJobList);
                configBuilder.get().build(simulationJobList);
            }
        } catch (Exception e) {
            // prepare 과정에서 문제가 발생하면 SIM_BOARD status를 ERROR로 변경
            String errorMsg = String.format("Exception occured in prepare");
            throw new IllegalStateException(errorMsg);
        }
    }

    private Optional<ConfigBuilder> setConfigBuilder(SimulatorCategory simulator) {
        switch (simulator) {
            case MCPSIM:
                return Optional.of(new McpsimConfigBuilder());
            default:
                return Optional.empty();
        }
    }

    public void prepareScenarioConfigFiles(SimulationJobList simulationJobList)
        throws IOException, JDOMException {

        // Set scenario config path
        String configPath =
            FileService.HISTORY_DIR_PATH + FileService.DIR_DELIMETER + simulationJobList.getUser()
                + FileService.DIR_DELIMETER + simulationJobList.getSimulator()
                + FileService.DIR_DELIMETER + simulationJobList.getFslName()
                + FileService.DIR_DELIMETER + FileService.CONFIG_DIR_NAME;
        simulationJobList.setConfigDirPath(configPath);

        if (!existInCurrentServer(simulationJobList)) {
            // 현재 서버에 없다면 다른 서버에 요청(요청 받는 서버에서 삭제까지 수행)
            System.out.println(
                String.format("Current Server(no: %d) does not have a Scenario config files.",
                    CURRENT_SERVER_TEMP));
            System.out.println(String.format("Request config files from Server: %d",
                simulationJobList.getExecution_server()));
            requestSimConfigFiles(simulationJobList);
            moveToConfigDirectory(simulationJobList);
        }
        setFilePathIntoJob(simulationJobList);
    }


    private boolean existInCurrentServer(SimulationJobList job) {
        return Objects.equals(job.getReservation_server(), CURRENT_SERVER_TEMP);
    }

    private void setFilePathIntoJob(SimulationJobList job) {
        List<String> fileNameList = fileService.getFileList(job.getConfigDirPath());
        fileNameList.forEach(fileName -> {
            String[] splited = fileName.split("\\.");
            String extension = splited[splited.length - 1];
            if (extension.equals("fsl")) {
                job.setFslFilePath(job.getConfigDirPath() + FileService.DIR_DELIMETER + fileName);
            } else if (extension.equals("fss")) {
                job.getSimulationMap().get(splited[0])
                    .setFssFilePath(job.getConfigDirPath() + FileService.DIR_DELIMETER + fileName);
            }
        });
    }

    private void requestSimConfigFiles(SimulationJobList job) {

    }

    private void moveToConfigDirectory(SimulationJobList job) {
        fileService.createDirectories(job.getConfigDirPath());
//        job.setConfigDirPath();
    }

    @Override
    public void executeJob(Job job) throws IOException {
        SimulationJobList simulationJobList = (SimulationJobList) job;

        Optional<Process> process = SimulationProcessFactory(simulationJobList);
        process.ifPresent(p -> simulationJobList.setProcess(p));
    }

    private Optional<Process> SimulationProcessFactory(SimulationJobList job) throws IOException {
        // 팩토리 메서드 패턴으로 수정하기 !!
        Runtime rt = Runtime.getRuntime();
        Process process = null;

        String executeCmd = String.format("java -jar %s/%s/%s/%s_%s.jar ",
            FileService.SIMULATOR_DIR_PATH, job.getSimulator(), job.getVersion(),
            job.getSimulator(), job.getVersion());
        String args = "";
        try {
            switch (SimulatorCategory.getCategoryByString(job.getSimulator())) {
                case MCPSIM:
                    args = String.format("%s %s", job.getSimulator(), job.getVersion());
                    process = rt.exec(executeCmd + args);
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
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

        return Optional.ofNullable(process);
    }

    @Override
    public void update(Subscribable s, Object arg) {
        System.out.println("update update update update update update update update ");
        Map<SimulationJobList, List<SimulationJob>> statusChangedJobs = (Map<SimulationJobList, List<SimulationJob>>) arg;

        try {
            for (SimulationJobList job : statusChangedJobs.keySet()) {
                job.getSimulationMap().values().forEach(simulationJob -> {
                    simBoardService.updateSimBoardRecord(simulationJob);
                });
                if (isProcessTerminated(job)) {
                    job.getSimulationMap().values().forEach(simulationJob -> {
                        // procedure: delete record from SIM_BOARD, add record to SIM_HISTORY
                        simHistoryService.moveFromBoardToHistory(simulationJob);
                    });
                    // remove job from progress monitor
                    progressMonitor.removeJob(job);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isProcessTerminated(SimulationJobList job) {
        return job.getProcess().isAlive();
    }
}
