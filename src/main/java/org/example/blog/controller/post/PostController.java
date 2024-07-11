package org.example.blog.controller.post;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.Image;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.post.PublishedType;
import org.example.blog.domain.user.History;
import org.example.blog.domain.user.User;
import org.example.blog.jwt.jwtUtil.JwtTokenizer;
import org.example.blog.service.blog.BlogService;
import org.example.blog.service.post.FileStorageService;
import org.example.blog.service.post.LikeService;
import org.example.blog.service.post.PostService;
import org.example.blog.service.user.HistoryService;
import org.example.blog.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private final FileStorageService fileStorageService;

    private final UserService userService;

    private final BlogService blogService;

    private final JwtTokenizer jwtTokenizer;

    private final LikeService likeService;

    private final HistoryService historyService;

    @GetMapping("/new")
    public String newPostForm(HttpServletRequest request, RedirectAttributes redirectAttributes,Model model) {
        User user = userService.findUserById(userService.getUserIdFromCookie(request));
        model.addAttribute("user", user);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        Blog blog = blogService.findBlogByUserId(user.getId());
        if (blog == null) {
            redirectAttributes.addFlashAttribute("error", "블로그를 찾을수 없습니다.");
            return "redirect:/login";
        }
        model.addAttribute("blog", blog);
        return "/view/post/newPost";
    }

    @PostMapping("/create")
    public String createPost(@RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam("tags") String tags,
                             @RequestParam("image") MultipartFile[] images,
                             @RequestParam(value = "published", required = false) String published,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {

        try {
            // 로그인한 사용자 쿠키로 확인
            User user = userService.findUserById(userService.getUserIdFromCookie(request));
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
                return "redirect:/login";
            }

            // 사용자 블로그
            Blog blog = blogService.findBlogByUserId(user.getId());
            if (blog == null) {
                redirectAttributes.addFlashAttribute("error", "블로그를 찾을 수 없습니다.");
                return "redirect:/blogs/create";
            }
            // 파일 저장 처리
            List<Image> imagePaths = Arrays.stream(images)
                    .map(file -> {
                        try {
                            Image image = new Image();
                            String filePath = fileStorageService.storeFile(file);
                            if (filePath == null) {
                                image.setFilePath(null);
                            }else image.setFilePath("/upload/" + filePath);

                            return image;
                        } catch (IOException e) {
                            throw new RuntimeException("파일 저장 중 오류 발생", e);
                        }
                    }).collect(Collectors.toList());
            PublishedType draft = published != null ? PublishedType.DRAFT : PublishedType.PUBLISHED;

            // 게시물 생성 처리
            postService.createPost(blog, title, content, tags, imagePaths,draft);

            redirectAttributes.addFlashAttribute("success", "게시물이 성공적으로 생성되었습니다!");
            return "redirect:/blogs/" + blogService.findBlogByUserId(user.getId()).getBlogId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "게시물 생성 중 오류가 발생했습니다: " + e.getMessage());
            System.out.println(e.getMessage());
            return "redirect:/posts/new";
        }
    }

    @GetMapping("/{id}")
    public String getPostById(@PathVariable("id") Long postId, Model model,HttpServletRequest request) {
        Post post = postService.getPostById(postId);
        Long userId = userService.getUserIdFromCookie(request);
        User user = userService.findUserById(userId);
        History history = new History();
        model.addAttribute("userId", userId);
        model.addAttribute("user", user);
        boolean likedByCurrentUser = likeService.isLikedByCurrentUser(post,userService.findUserById(userId));
        if (post == null) {
            model.addAttribute("error", "게시글을 찾을 수 없습니다.");
            return "/view/error";
        } else {
            history.setPost(post);
            history.setUser(user);
            historyService.saveHistory(history,userId,postId);
            model.addAttribute("post", post);
            model.addAttribute("blog", blogService.findBlogByUserId(userId));
            model.addAttribute("likedByCurrentUser", likedByCurrentUser);
            if (blogService.getBlogByPostId(postId).getUser().getId() != userId) {
                List<History> currentUserHistories = historyService.getHistoryByUserId(userId);
                for (History currentHistory : currentUserHistories) {
                    if (currentHistory.getPost().getPostId() != postId && !Objects.equals(currentHistory.getViewDay(), LocalDate.now())) {
                        post.setView(post.getView() + 1);
                        postService.savePost(post);
                    }
                }
            }
            return "/view/post/postDetail";
        }
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Long postId, Model model,HttpServletRequest request) {
        Long blogId = blogService.getBlogByPostId(postId).getBlogId();
        Cookie[] cookies = request.getCookies();
        String accessToken = null;
        if(cookies != null){
            for(Cookie cookie : cookies){
                if("accessToken".equals(cookie.getName())){
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        Long userId = jwtTokenizer.getUserIdFromToken(accessToken);


        if (userId != null) {
            if (blogService.findBlogByUserId(userId).getBlogId() == blogId) {
                postService.deletePostById(postId);
            }
        }
        model.addAttribute("blogId",blogId);
        return "redirect:/blogs/" + blogId;
    }
}
