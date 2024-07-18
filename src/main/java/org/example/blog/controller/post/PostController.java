package org.example.blog.controller.post;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.Image;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.post.PublishedType;
import org.example.blog.domain.post.Series;
import org.example.blog.domain.user.History;
import org.example.blog.domain.user.User;
import org.example.blog.jwt.jwtUtil.JwtTokenizer;
import org.example.blog.service.blog.BlogService;
import org.example.blog.service.post.FileStorageService;
import org.example.blog.service.post.LikeService;
import org.example.blog.service.post.PostService;
import org.example.blog.service.post.postInterface.SeriesInterface;
import org.example.blog.service.user.HistoryService;
import org.example.blog.service.user.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


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
    private final SeriesInterface seriesService;

    @GetMapping("/new")
    public String newPostForm(HttpServletRequest request, RedirectAttributes redirectAttributes,Model model) {
        User user = userService.findUserById(userService.getUserIdFromCookie(request));
        Blog blog = blogService.findBlogByUserId(user.getId());
        model.addAttribute("user", user);
        Set<Series> allSeries = new HashSet<>();
        if (seriesService.getSeriesByBlogId(blog.getBlogId()) != null) {
            allSeries = seriesService.getSeriesByBlogId(blog.getBlogId());
        }

        model.addAttribute("allSeries", allSeries);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }

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
                             @RequestParam(value = "status", required = false) String status,
                             @RequestParam(name = "series", required = false) String series,
                             @RequestParam(value = "newSeriesName", required = false) String newSeriesName,
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
            List<Image> imagePaths = fileStorageService.imagePaths(images);
            PublishedType draft = published != null ? PublishedType.DRAFT : PublishedType.PUBLISHED;
            boolean isPublic = status != null;
            // 게시물 생성 처리
            postService.createPost(blog, title, content, tags, imagePaths, draft, isPublic, series, newSeriesName);

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

        model.addAttribute("userId", userId);
        model.addAttribute("user", user);
        boolean likedByCurrentUser = likeService.isLikedByCurrentUser(post,userService.findUserById(userId));
        if (post == null) {
            model.addAttribute("error", "게시글을 찾을 수 없습니다.");
            return "/view/error";
        } else {
            // 조회수 처리
            postService.postView(postId,userId);

            History history = new History();
            historyService.saveHistory(history,user,post);
            int likes = likeService.findLikesByPost(post).size();
            model.addAttribute("post", post);
            model.addAttribute("blog", blogService.findBlogByUserId(userId));
            model.addAttribute("likedByCurrentUser", likedByCurrentUser);
            model.addAttribute("likes", likes);


            return "/view/post/postDetail";
        }
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Long postId,HttpServletRequest request) {
        Long blogId = postService.getBlogByPostId(postId).getBlogId();
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
            if (Objects.equals(blogService.findBlogByUserId(userId).getBlogId(), blogId)) {
                postService.deletePostById(postId);
            }
        }
        return "redirect:/blogs/" + blogId;
    }

    @GetMapping("/update/{id}")
    public String showEditPostForm(@PathVariable("id") Long postId, Model model,HttpServletRequest request) {
        Post post = postService.getPostById(postId);
        if (!Objects.equals(userService.getUserIdFromCookie(request), post.getBlog().getUser().getId())) {
            model.addAttribute("error", "권한이 없습니다");
            return "/view/error";
        }
        model.addAttribute("post", post);
        model.addAttribute("user",userService.findUserById(userService.getUserIdFromCookie(request)));
        Set<Series> allSeries = seriesService.getSeriesByBlogId(post.getBlog().getBlogId());
        model.addAttribute("allSeries", allSeries);
        return "/view/post/editPost";
    }

    @PostMapping("/update/{id}")
    public String editPost(@PathVariable("id") Long postId,
                           @RequestParam("title") String title,
                           @RequestParam("content") String content,
                           @RequestParam("tags") String tags,
                           @RequestParam("image") MultipartFile[] images,
                           @RequestParam(value = "published", required = false) String published,
                           @RequestParam(value = "status", required = false) String status,
                           @RequestParam("series") String series,
                           @RequestParam(value = "newSeriesName", required = false) String newSeriesName,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findUserById(userService.getUserIdFromCookie(request));
//            Blog blog = blogService.findBlogByUserId(currentUser.getId());
//            Post post = postService.getPostById(postId);
            // 파일 저장 처리
            List <Image> imagePaths = fileStorageService.postImagePaths(images,postId);
            PublishedType draft = published.equals("on,true") ? PublishedType.DRAFT : PublishedType.PUBLISHED;
            boolean isPublic = !status.equals("false");
            // 게시물 생성 처리
            postService.updatePost(postId, title, content, tags, imagePaths, draft, isPublic,series,newSeriesName);

            return "redirect:/blogs/" + blogService.findBlogByUserId(user.getId()).getBlogId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "게시물 생성 중 오류가 발생했습니다: " + e.getMessage());
            System.out.println(e.getMessage());
            return "redirect:/posts/"+postId;
        }
    }
}
