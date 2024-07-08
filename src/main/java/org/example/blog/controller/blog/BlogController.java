package org.example.blog.controller.blog;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.user.User;
import org.example.blog.jwt.jwtUtil.JwtTokenizer;
import org.example.blog.service.blog.BlogService;
import org.example.blog.service.post.PostService;
import org.example.blog.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/blogs")
public class BlogController {
    private final UserService userService;
    private final BlogService blogService;
    private final PostService postService;
    private final JwtTokenizer jwtTokenizer;

    @GetMapping()
    public String showMainPage(Model model){
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        return "/view/blog/mainPage";
    }

    @GetMapping("/createform")
    public String createBlogForm(HttpServletRequest request, Model model) {
        Long id = userService.getUserIdFromCookie(request); // 쿠키
        if (id != null) {
            User user = userService.findUserById(id);
            if (user != null) {
                model.addAttribute("userId", id);
                return "/view/blog/createBlog";
            }
        }
        return "redirect:/api/login";
    }

    @PostMapping("/create")
    public String createBlog(@RequestParam("title") String title,
                             @RequestParam("userId") Long userId,
                             RedirectAttributes redirectAttributes) {
        try {
            Long blogId = blogService.createBlog(title, userId);
            redirectAttributes.addFlashAttribute("success", "블로그가 생성되었습니다.");
            return "redirect:/blogs/" + blogId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/blogs/create";
        }
    }

    @GetMapping("/{id}")
    public String getBlogById(@PathVariable("id") Long id,
                              Model model,
                              HttpServletRequest request) {
        Long userId = userService.getUserIdFromCookie(request); // 쿠키
        Long blogId = null;
        if (userId != null) {
            blogId = blogService.findBlogByUserId(userId).getBlogId();
        }

        if (blogId == null || !blogId.equals(id) || userId == null) {
            model.addAttribute("error", "권한이 없습니다.");
            return "/view/error";
        }
        Blog blog = blogService.findBlogByUserId(userId);
        if (blog != null) {
            blogService.sortedPosts(blogId);
            model.addAttribute("blog", blog);
            return "/view/blog/blog";
        } else {
            model.addAttribute("error", "블로그를 찾지 못했습니다.");
            return "redirect:/login";
        }
    }
}