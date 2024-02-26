package com.samsung.portalserver.service;

import static com.samsung.portalserver.service.FileConstants.SIMULATOR_DIR_PATH;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileServiceTest {

    FileService fileService = new FileService();

    @Test
    void zipFiles() {
        List<String> filePaths = new ArrayList<>();
        filePaths.add("/Users/js.oh/Desktop/sample_input.txt");
        filePaths.add("/Users/js.oh/Desktop/삼성전자 자기소개서.docx");

        fileService.makeZipFiles(filePaths);
    }

    @DisplayName("시뮬레이터 엔진 개수 확인 테스트")
    @Test
    void simulatorInstallTest() {

        /**
         * 유효하지 않은 path의 파일 개수 = 0
         */
        ArrayList<String> unvalidResult = fileService.getFileList("unvalidPath");
        assertThat(unvalidResult.size()).isEqualTo(0);

        /**
         * simulator 종류 = 3 (AMHS_SIM, SeeFlow, REMOTE_SIM)
         * Fail일 경우 PATH 설정 필요
         */
        ArrayList<String> fileList = fileService.getFileList(SIMULATOR_DIR_PATH);
        fileList.forEach(s -> System.out.println(s));

        assertThat(fileList.size()).isEqualTo(6);
    }
}