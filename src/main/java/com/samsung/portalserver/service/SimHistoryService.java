package com.samsung.portalserver.service;

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
        private String scenario;
        private String simulator;
        private String version;
        private Integer completedRep;
        private Integer requestRep;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private List<String> terminationReason = new ArrayList<>();
        private Integer executionServer;

        public MyHistoryDto(Long no, SimHistory simHistory) {
            this.key = no;
            this.no = no;
            this.scenario = simHistory.getScenario();
            this.simulator = simHistory.getSimulator();
            this.version = simHistory.getVersion();
            this.completedRep = simHistory.getCompleted_rep();
            this.requestRep = simHistory.getRequest_rep();
            this.startDate = simHistory.getStart_date();
            this.endDate = simHistory.getEnd_date();
            this.terminationReason.add(simHistory.getTermination_reason());
            this.executionServer = simHistory.getExecution_server();
        }
    }

    public void downloadMyHistory(String user, String simulator, String scenario,
        HttpServletResponse response) {
        user = "USER1";
        simulator = "AMHS Sim";
        scenario = "Scenario1";
        String directoryPathToZip =
            "/Users/js.oh/Desktop/Developers/simportal/history" + "/" + user + "/" + simulator + "/"
                + scenario;
        String downloadFileName = simulator + "+" + scenario + ".zip"; // 다운로드 파일 이름 명시
        String tempDirectoryPath = "/Users/js.oh/Desktop/Developers/simportal/temp/";

        ZipUtil.pack(new File(directoryPathToZip), new File(tempDirectoryPath + downloadFileName));

        File file = new File(tempDirectoryPath + downloadFileName);

        response.setHeader("Content-Disposition",
            "attachment; filename=\"" + downloadFileName + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary"); //
        response.setHeader("Content-Type", "application/zip"); // file type
        response.setHeader("Content-Length", "" + file.length()); // 응답의 크기 명시
        response.setHeader("Pragma", "no-cache;");
        response.setHeader("Expires", "-1;");

        fileService.setFileIntoResponse(tempDirectoryPath, downloadFileName, response);

        fileService.deleteFile(tempDirectoryPath, downloadFileName);
    }

    public Long moveFromBoardToHistory(SimulationJob job) {
        return simHistoryRepository.moveFromBoardToHistory(job);
    }
}
