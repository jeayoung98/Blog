package org.example.blog.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.user.History;
import org.example.blog.domain.user.User;
import org.example.blog.service.blog.BlogService;
import org.example.blog.service.post.LikeService;
import org.example.blog.service.post.PostService;
import org.example.blog.service.user.HistoryService;
import org.example.blog.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {
    private final UserService userService;
    private final BlogService blogService;
    private final LikeService likeService;
    private final PostService postService;
    private final HistoryService historyService;

    @GetMapping("/user/{userId}")
    public String userHistory(@PathVariable("userId") Long userId, Model model) {
        User user = userService.findUserById(userId);
        Blog blog = blogService.findBlogByUserId(userId);
        model.addAttribute("user", user);
        model.addAttribute("blog", blog);

        List<History> histories = historyService.getHistoriesByUserIdByViewDayAsc(userId);
        model.addAttribute("histories", histories);

        return "/view/user/userHistories";
    }

    @GetMapping("/like/{userId}")
    public String likeHistory(@PathVariable("userId") Long userId, Model model) {
        User user = userService.findUserById(userId);
        Blog blog = blogService.findBlogByUserId(userId);
        model.addAttribute("user", user);
        model.addAttribute("blog", blog);

        List<Post> posts = likeService.getLikesPosts(user);
        model.addAttribute("posts", posts);

        return "/view/user/userLikes";
    }

}
