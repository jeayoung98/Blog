package org.example.blog.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Service
public class FileStorageService {
    private final String uploadDir = "C://Temp/upload/";

    public String storeFile(MultipartFile file) throws IOException {
        // 파일 저장 디렉토리 경로 설정
        Path uploadPath = Paths.get(uploadDir);

        // 저장할 파일의 경로 설정
        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // 파일 저장
        Files.copy(file.getInputStream(), filePath);
        System.out.println("저장완료 !!");
        return fileName;
    }
}
