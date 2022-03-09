package com.samsung.portalserver.service;

import static com.samsung.portalserver.service.FileConstants.DIR_DELIMETER;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;


public class FileService {


    public void makeZipFiles(List<String> filePathIncludeNameAndExtention) {
        File resultZip = new File("/Users/js.oh/Desktop/result.zip");
        byte[] buf = new byte[4096];

        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(resultZip))) {
            for (String path : filePathIncludeNameAndExtention) {
                File file = new File(path);

                try (FileInputStream in = new FileInputStream(file)) {
                    ZipEntry ze = new ZipEntry(file.getName());
                    out.putNextEntry(ze);

                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    out.closeEntry();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setFileIntoResponse(String filePath, String fileNameIncludeExtention,
        HttpServletResponse response) {
        try (FileInputStream fis = new FileInputStream(
            filePath + fileNameIncludeExtention); OutputStream out = response.getOutputStream();) {
            int readCount = 0;
            byte[] buffer = new byte[1024];
            while ((readCount = fis.read(buffer)) != -1) {
                out.write(buffer, 0, readCount); // response.getOutputStream()에 전송할 file 입력
            }
        } catch (Exception ex) {
            throw new RuntimeException("file Save Error");
        }
    }

    public ArrayList<String> getFileList(String directoryPath) {
        File dir = new File(directoryPath);
        ArrayList<String> fileList = new ArrayList<>();

        if (dir.length() > 0) {
            fileList = (ArrayList<String>) Arrays.stream(Objects.requireNonNull(dir.list()))
                .filter(file -> file.charAt(0) != '.') // .DS_Store 같은 시스템이 생성하는 파일 제외
                .collect(Collectors.toList());
        }
        return fileList;
    }

    public boolean aleadyExistFileOrDir(String path) {
        File file = new File(path);
        return file.exists();
    }

    public void saveMultipartFileToLocal(String directoryPath, MultipartFile fslFile,
        List<MultipartFile> fssFiles) {
        try {
            File fsl = new File(directoryPath + DIR_DELIMETER + fslFile.getOriginalFilename());
            fslFile.transferTo(fsl);
            for (MultipartFile fssFile : fssFiles) {
                File fss = new File(directoryPath + DIR_DELIMETER + fssFile.getOriginalFilename());
                fssFile.transferTo(fss);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createDirectories(String path) {
        Path dirPath = Paths.get(path);
        try {
            Files.createDirectories(dirPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(String filePath, String fileNameIncludeExtention) {
        try {
            Path path = Paths.get(filePath + fileNameIncludeExtention);
            Files.delete(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDirectory(String directroyPath) {
        try {
            File deletePath = new File(directroyPath);
            FileUtils.deleteDirectory(deletePath);
        } catch (IllegalArgumentException e) {
            System.out.println("삭제 대상이 Directory가 아닙니다");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
