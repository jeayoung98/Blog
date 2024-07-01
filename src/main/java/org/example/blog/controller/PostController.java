package org.example.blog.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blog.domain.Blog;
import org.example.blog.domain.Image;
import org.example.blog.domain.Post;
import org.example.blog.domain.User;
import org.example.blog.service.BlogService;
import org.example.blog.service.FileStorageService;
import org.example.blog.service.PostService;
import org.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/new")
    public String newPostForm(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User user = userService.findUserById(userService.getUserIdFromCookie(request));
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/api/login";
        }
        Blog blog = blogService.getBlogByUserId(user.getId());
        if (blog == null) {
            redirectAttributes.addFlashAttribute("error", "블로그를 찾을수 없습니다.");
            return "redirect:/api/login";
        }
        return "/view/newPost";
    }

    @PostMapping("/create")
    public String createPost(@RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam("tags") String tags,
                             @RequestParam("image") MultipartFile[] images,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {

        try {
            // 로그인한 사용자 쿠키로 확인
            User user = userService.findUserById(userService.getUserIdFromCookie(request));
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
                return "redirect:/api/login";
            }

            // 사용자 블로그
            Blog blog = blogService.getBlogByUserId(user.getId());
            if (blog == null) {
                redirectAttributes.addFlashAttribute("error", "블로그를 찾을 수 없습니다.");
                return "redirect:/api/blogs/create";
            }
            // 파일 저장 처리
            List<Image> imagePaths = Arrays.stream(images)
                    .map(file -> {
                        try {
                            Image image = new Image();
                            String filePath = fileStorageService.storeFile(file);
                            System.out.println(filePath);
                            if (filePath == null) {
                                image.setFilePath(null);
                            }else image.setFilePath("/C://Temp/upload/" + filePath);

                            return image;
                        } catch (IOException e) {
                            throw new RuntimeException("파일 저장 중 오류 발생", e);
                        }
                    }).collect(Collectors.toList());

            // 게시물 생성 처리
            postService.createPost(blog, title, content, tags, imagePaths);

            redirectAttributes.addFlashAttribute("success", "게시물이 성공적으로 생성되었습니다!");
            return "redirect:/api/blogs/" + blogService.getBlogByUserId(user.getId()).getBlogId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "게시물 생성 중 오류가 발생했습니다: " + e.getMessage());
            System.out.println(e.getMessage());
            return "redirect:/api/posts/new";
        }
    }

    @GetMapping("/{id}")
    public String getPostById(@PathVariable("id") Long postId, Model model) {
        Post post = postService.getPostById(postId);
        if (post == null) {
            model.addAttribute("error", "게시글을 찾을 수 없습니다.");
            return "/view/error";
        } else {
            model.addAttribute("post", post);
            return "/view/postDetail";
        }
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Long postId, Model model,@CookieValue(value="userId" , defaultValue = "") Long userId) {
        Long blogId = blogService.getBlogByPostId(postId).getBlogId();
        if (userId != null) {
            if (blogService.getBlogByUserId(userId).getBlogId() == blogId) {
                postService.deletePostById(postId);
            }
        }
        model.addAttribute("blogId",blogId);
        return "redirect:/api/blogs/" + blogId;
    }
}