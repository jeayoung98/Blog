package org.example.blog.controller.blog;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.post.PublishedType;
import org.example.blog.domain.user.User;
import org.example.blog.service.blog.BlogService;
import org.example.blog.service.post.PostService;
import org.example.blog.service.user.UserService;
import org.example.blog.service.user.userInterface.FollowInterface;
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
    private final FollowInterface followService;

    @GetMapping()
    public String showMainPage(HttpServletRequest request,Model model){
        List<Post> posts = postService.getAllPostByPublished(PublishedType.PUBLISHED);
        Long id = userService.getUserIdFromCookie(request);
        User user = userService.findUserById(id);

        model.addAttribute("posts", posts);
        model.addAttribute("user", user);
        model.addAttribute("blog", blogService.findBlogByUserId(id));
        return "/view/blog/mainPage";
    }

    @GetMapping("/createform")
    public String createBlogForm(HttpServletRequest request, Model model) {
        Long id = userService.getUserIdFromCookie(request); // 쿠키
        if (id != null) {
            User user = userService.findUserById(id);
            if (user != null) {
                model.addAttribute("userId", id);
                model.addAttribute("user", user);
                model.addAttribute("blog", blogService.findBlogByUserId(id));
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
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        System.out.println("블로그 페이지 요청 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
        Long currentUserId = userService.getUserIdFromCookie(request); // 쿠키
        Long blogId = null;
        Long userId = userService.findUserById(blogService.getBlogById(id).getUser().getId()).getId();
        User user = userService.findUserById(userId);
        User currentUser = userService.findUserById(currentUserId);
        List<User> follower = followService.getFollowsByFollower(user);
        List<User> following = followService.getFollowsByFollowing(user);
        boolean isFollowing = followService.isFollowing(following,currentUser);

        model.addAttribute("follower", follower);
        model.addAttribute("following", following);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("user", currentUser);
        model.addAttribute("blogOwner", user);
        if (userId != null) {
            try {
                blogId = blogService.findBlogByUserId(userId).getBlogId();

            } catch (NullPointerException e) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            }
        }

//        if (blogId == null || !blogId.equals(id) || currentUserId == null) {
//            model.addAttribute("error", "권한이 없습니다.");
//            return "/view/error";
//        }
        Blog blog = blogService.findBlogByUserId(userId);
        if (blog != null) {
            blogService.sortedPosts(blogId);
            model.addAttribute("post", postService.getAllPostByPublished(PublishedType.PUBLISHED));
            model.addAttribute("blog", blog);
            return "/view/blog/blog";
        } else {
            model.addAttribute("error", "블로그를 찾지 못했습니다.");
            return "redirect:/login";
        }
    }

    @GetMapping("/draft/{id}")
    public String showDraftPosts(@PathVariable("id") Long blogId, Model model,HttpServletRequest request) {
        User user = userService.findUserById(userService.getUserIdFromCookie(request));
        model.addAttribute("user",user);
        model.addAttribute("blog", blogService.getBlogById(blogId));
        model.addAttribute("posts", postService.getDraftPostsByBlog(blogId));
        return "/view/blog/draft";
    }
}