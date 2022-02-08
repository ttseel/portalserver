package com.samsung.portalserver.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest
class SimBoardServiceTest {

    SimReservationService simReservationService = new SimReservationService(null, null);
    FileService fileService = new FileService();
//    SimBoardService simBoardService;
//    @Autowired
//    public SimBoardServiceTest(SimBoardService simBoardService) {
//        this.simBoardService = simBoardService;
//    }



    MultipartFile multipartFile = new MockMultipartFile("MOCK_FILE", "MOCK_FILE.txt", null, new byte[10]);
    @Test
    void saveScenarioFile() {
        String saveDirectoryPath = FileService.HISTORY_DIR_PATH
                + FileService.DIR_DELIMETER + "TESTCODE_USER"
                + FileService.DIR_DELIMETER + "TESTCODE_SIMULATOR"
                + FileService.DIR_DELIMETER + "TESTCODE_SCENARIO";

        simReservationService.saveScenarioFile(saveDirectoryPath, multipartFile);

        assertThat(fileService.aleadyExistFileOrDir(saveDirectoryPath)).isEqualTo(true);

        fileService.deleteDirectory(
                FileService.HISTORY_DIR_PATH + FileService.DIR_DELIMETER + "TESTCODE_USER"
        );
    }
}