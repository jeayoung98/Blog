package org.example.blog.service.post;

import lombok.RequiredArgsConstructor;
import org.example.blog.domain.Image;
import org.example.blog.service.post.postInterface.ImageInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final String uploadDir = "C://Temp/upload/";
    private final ImageInterface imageService;

    @Transactional
    public String storeFile(MultipartFile file) throws IOException {
        // 파일 저장 디렉토리 경로 설정
        Path uploadPath = Paths.get(uploadDir);

        // 저장할 파일의 경로 설정
        if (file.isEmpty()) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        String formattedDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String fileName = file.getOriginalFilename();

        // 파일 확장자 분리
        String fileExtension = "";
        if (fileName.contains(".")) {
            fileExtension = fileName.substring(fileName.lastIndexOf("."));
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }

        // 새로운 파일 이름 생성
        String newFileName = fileName + "_" + formattedDateTime + fileExtension;
        Path filePath = uploadPath.resolve(newFileName);


        // 파일 저장
        Files.copy(file.getInputStream(), filePath);
        return newFileName;
    }
    public List<Image> postImagePaths(MultipartFile[] images,Long postId) {
        List<Image> imagePaths = Arrays.stream(images)
                .map(file -> {
                    try {
                        Image image = imageService.getImageByPostId(postId);
                        String filePath = storeFile(file);
                        if (filePath == null) {
                            image.setFilePath(null);
                        }else image.setFilePath("/upload/" + filePath);

                        return image;
                    } catch (IOException e) {
                        throw new RuntimeException("파일 저장 중 오류 발생", e);
                    }
                }).collect(Collectors.toList());
        return imagePaths;
    }

    public List<Image> imagePaths(MultipartFile[] images) {
        List<Image> imagePaths = Arrays.stream(images)
                .map(file -> {
                    try {
                        Image image = new Image();
                        String filePath = storeFile(file);
                        if (filePath == null) {
                            image.setFilePath(null);
                        }else image.setFilePath("/upload/" + filePath);

                        return image;
                    } catch (IOException e) {
                        throw new RuntimeException("파일 저장 중 오류 발생", e);
                    }
                }).collect(Collectors.toList());
        return imagePaths;
    }
}
