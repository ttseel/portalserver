package com.samsung.portalserver.service;

import static com.samsung.portalserver.service.FileConstants.*;
import static com.samsung.portalserver.service.FileConstants.DIR_DELIMETER;
import static com.samsung.portalserver.service.FileConstants.HISTORY_DIR_PATH;

import com.samsung.portalserver.domain.SimHistory;
import com.samsung.portalserver.repository.SimHistoryRepository;
import com.samsung.portalserver.schedule.job.SimulationJob;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeroturnaround.zip.ZipUtil;

@Service
@Transactional
public class SimHistoryService {

    private final SimHistoryRepository simHistoryRepository;
    private final FileService fileService = new FileService();

    @Autowired
    public SimHistoryService(SimHistoryRepository simHistoryRepository) {
        this.simHistoryRepository = simHistoryRepository;
    }

    public Optional<List<MyHistoryDto>> readMyHistory(String user) {
        Optional<List<SimHistory>> simHistories = simHistoryRepository.readByUser(user);

        List<MyHistoryDto> myHistoryDto = new ArrayList<>();
        long idx = 1;
        if (simHistories.isPresent()) {
            for (SimHistory simHistory : simHistories.get()) {
                myHistoryDto.add(new MyHistoryDto(idx, simHistory));
                idx++;
            }
        }

        return Optional.ofNullable(myHistoryDto);
    }

    @Data
    public static class MyHistoryDto {

        private Long key;
        private Long no;
        private String group;
        private String scenario;
        private String simulator;
        private String version;
        private Integer endRep;
        private Integer requestRep;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private List<String> terminationReason = new ArrayList<>();
        private Integer executionServer;

        public MyHistoryDto(Long no, SimHistory simHistory) {
            this.key = no;
            this.no = no;
            this.group = simHistory.getFsl_name();
            this.scenario = simHistory.getScenario();
            this.simulator = simHistory.getSimulator();
            this.version = simHistory.getVersion();
            this.endRep = simHistory.getCompleted_rep();
            this.requestRep = simHistory.getRequest_rep();
            this.startDate = simHistory.getStart_date();
            this.endDate = simHistory.getEnd_date();
            this.terminationReason.add(simHistory.getTermination_reason());
            this.executionServer = simHistory.getExecution_server();
        }
    }

    public void downloadMyHistory(String user, String simulator, String group, String scenario,
        HttpServletResponse response) {
//        user = "USER2";
//        simulator = "MCPSIM";
//        group = "ScenarioList7";
//        scenario = "Scenario1";
        String directoryPathToZip =
            HISTORY_DIR_PATH + DIR_DELIMETER + user + DIR_DELIMETER + simulator + DIR_DELIMETER
                + group + DIR_DELIMETER + scenario;
        String downloadFileName = scenario + EXTENSION_ZIP; // 다운로드 파일 이름 명시

        ZipUtil.pack(new File(directoryPathToZip),
            new File(TEMP_DIR_PATH + DIR_DELIMETER + downloadFileName));

        File file = new File(TEMP_DIR_PATH + DIR_DELIMETER + downloadFileName);

        response.setHeader("Content-Disposition",
            "attachment; filename=\"" + downloadFileName + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary"); //
        response.setHeader("Content-Type", "application/zip"); // file type
        response.setHeader("Content-Length", "" + file.length()); // 응답의 크기 명시
        response.setHeader("Pragma", "no-cache;");
        response.setHeader("Expires", "-1;");

        fileService.setFileIntoResponse(TEMP_DIR_PATH, downloadFileName, response);

        fileService.deleteFile(TEMP_DIR_PATH, downloadFileName);
    }

    public Long moveFromBoardToHistory(SimulationJob job) {
        return simHistoryRepository.moveFromBoardToHistory(job);
    }
}
