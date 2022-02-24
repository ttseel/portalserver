package com.samsung.portalserver.service;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FileServiceTest {

    FileService fileService = new FileService();

    @Test
    void zipFiles() {
        List<String> filePaths = new ArrayList<>();
        filePaths.add("/Users/js.oh/Desktop/sample_input.txt");
        filePaths.add("/Users/js.oh/Desktop/삼성전자 자기소개서.docx");

        fileService.makeZipFiles(filePaths);
    }

    @Test
    void getSimulatorStoragePath() {
        System.out.println(FileService.FILE_DIR_ROOT_PATH);
        System.out.println(FileService.HISTORY_DIR_PATH);
        System.out.println(FileService.SIMULATOR_DIR_PATH);
    }

    @Test
    void getFileList() {

        /**
         * 유효하지 않은 path의 파일 개수 = 0
         */
        ArrayList<String> unvalidResult = fileService.getFileList("unvalidPath");
        assertThat(unvalidResult.size()).isEqualTo(0);

        /**
         * simulator 종류 = 3 (AMHS_SIM, SeeFlow, REMOTE_SIM)
         * Fail일 경우 PATH 설정 필요
         */
        ArrayList<String> fileList = fileService.getFileList(FileService.SIMULATOR_DIR_PATH);

        assertThat(fileList.size()).isEqualTo(3);
    }
}