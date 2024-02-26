package com.samsung.portalserver.service;

import static com.samsung.portalserver.service.FileConstants.DIR_DELIMETER;
import static com.samsung.portalserver.service.FileConstants.HISTORY_DIR_PATH;
import static org.assertj.core.api.Assertions.assertThat;

import com.samsung.portalserver.domain.SimBoard;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
class SimBoardServiceTest {

    ReservationService reservationService = new ReservationService(null, null);
    FileService fileService = new FileService();
    SimBoardService simBoardService;

    @Autowired
    public SimBoardServiceTest(SimBoardService simBoardService) {
        this.simBoardService = simBoardService;
    }

    MultipartFile fslFile;
    List<MultipartFile> fssFiles;

    @BeforeEach
    void before() {
        fslFile = new MockMultipartFile("MOCK_FSL", "FSL.fsl", null, new byte[10]);
        fssFiles = new ArrayList();
        fssFiles.add(new MockMultipartFile("MOCK_FSS1", "FSS1.fss", null, new byte[10]));
        fssFiles.add(new MockMultipartFile("MOCK_FSS2", "FSS2.fss", null, new byte[10]));
        fssFiles.add(new MockMultipartFile("MOCK_FSS3", "FSS3.fss", null, new byte[10]));
    }

    @Test
    void saveScenarioFile() {
        String saveDirectoryPath =
            HISTORY_DIR_PATH + DIR_DELIMETER + "TESTCODE_USER" + DIR_DELIMETER
                + "TESTCODE_SIMULATOR" + DIR_DELIMETER + "TESTCODE_SCENARIO";

        reservationService.saveScenarioFile(saveDirectoryPath, fslFile, fssFiles);

        assertThat(fileService.aleadyExistFileOrDir(saveDirectoryPath)).isEqualTo(true);

        fileService.deleteDirectory(HISTORY_DIR_PATH + DIR_DELIMETER + "TESTCODE_USER");
    }

    @Test
    @Transactional
//    @Rollback(false)
    void updateCurrentRep() {
        Optional<SimBoard> simBoard = simBoardService.readUniqueRecord(72);

        if (!simBoard.isPresent()) {
            throw new IllegalStateException("Test record has been deleted");
        }

        int beforeRep = simBoard.get().getCurrent_rep();
        int afterRep = beforeRep + 1;
        simBoard.ifPresent(sb -> sb.setCurrent_rep(afterRep));
        simBoardService.commitSimBoard();

        Optional<SimBoard> simBoardAfterUpdate = simBoardService.readUniqueRecord(72);

        assertThat(afterRep).isEqualTo(simBoardAfterUpdate.get().getCurrent_rep());
    }
}