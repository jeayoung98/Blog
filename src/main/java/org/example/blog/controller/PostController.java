package org.example.blog.controller;

import org.example.blog.domain.Image;
import org.example.blog.service.FileStorageService;
import org.example.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/create")
    public String createPost(@RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam("tags") String tags,
                             @RequestParam("image") MultipartFile[] images,
                             RedirectAttributes redirectAttributes) {
        try {
            // 파일 저장 처리
            List<Image> imagePaths = Arrays.stream(images)
                    .map(file -> {
                        try {
                            String filePath = fileStorageService.storeFile(file);
                            Image image = new Image();
                            image.setFilePath(filePath);
                            return image;
                        } catch (IOException e) {
                            throw new RuntimeException("파일 저장 중 오류 발생", e);
                        }
                    }).collect(Collectors.toList());

            // 게시물 생성 처리
            postService.createPost(title, content, tags, imagePaths);

            redirectAttributes.addFlashAttribute("message", "게시물이 성공적으로 생성되었습니다!");
            return "redirect:/api/posts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "게시물 생성 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/api/posts/new";
        }
    }
}

