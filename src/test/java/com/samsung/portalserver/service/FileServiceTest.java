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
        System.out.println(FileConstants.FILE_DIR_ROOT_PATH);
        System.out.println(FileConstants.HISTORY_DIR_PATH);
        System.out.println(FileConstants.SIMULATOR_DIR_PATH);
    }
}
