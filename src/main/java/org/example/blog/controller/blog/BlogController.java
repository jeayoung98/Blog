package org.example.blog.controller.blog;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.post.PublishedType;
import org.example.blog.domain.post.Series;
import org.example.blog.domain.user.User;
import org.example.blog.service.blog.BlogService;
import org.example.blog.service.post.PostService;
import org.example.blog.service.post.SeriesService;
import org.example.blog.service.user.UserService;
import org.example.blog.service.user.userInterface.FollowInterface;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/blogs")
public class BlogController {
    private final UserService userService;
    private final BlogService blogService;
    private final PostService postService;
    private final FollowInterface followService;
    private final SeriesService seriesService;

    @GetMapping()
    public String showMainPage(HttpServletRequest request, Model model) {
        List<Post> posts = postService.getAllPostByPublished(PublishedType.PUBLISHED);
        Long id = userService.getUserIdFromCookie(request);
        User user = userService.findUserById(id);

        List<Post> allPosts = postService.getAllPosts(PublishedType.PUBLISHED, true);
        model.addAttribute("allPosts", allPosts);
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
        Set<User> following = followService.getFollowsByFollower(user);
        Set<User> follower = followService.getFollowsByFollowing(user);
        boolean isFollowing = followService.isFollowing(following, currentUser);

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

        Blog blog = blogService.findBlogByUserId(userId);
        if (blog != null) {
            blogService.sortedPosts(blogId);
            model.addAttribute("posts", postService.getPostsOrderByTime(blog));
            model.addAttribute("blog", blog);
            return "/view/blog/blog";
        } else {
            model.addAttribute("error", "블로그를 찾지 못했습니다.");
            return "redirect:/login";
        }
    }

    @GetMapping("/draft/{id}")
    public String showDraftPosts(@PathVariable("id") Long blogId, Model model, HttpServletRequest request) {
        User user = userService.findUserById(userService.getUserIdFromCookie(request));
        model.addAttribute("user", user);
        model.addAttribute("blog", blogService.getBlogById(blogId));
        model.addAttribute("posts", postService.getDraftPostsByBlog(blogId));
        return "/view/blog/draft";
    }

    @GetMapping("/sort/date/{id}")
    public String recentPosts(@PathVariable(name = "id") Long blogId, Model model, HttpServletRequest request) {
        Blog blog = blogService.getBlogById(blogId);
        List<Post> posts = postService.getAllPosts(PublishedType.PUBLISHED, true);
        Long userId = userService.getUserIdFromCookie(request);
        User blogOwner = userService.findUserById(blogService.getBlogById(blogId).getUser().getId());
        Set<User> following = followService.getFollowsByFollower(blogOwner);
        Set<User> follower = followService.getFollowsByFollowing(blogOwner);
        User user = userService.findUserById(userId);
        boolean isFollowing = followService.isFollowing(following, user);

        model.addAttribute("follower", follower);
        model.addAttribute("following", following);
        model.addAttribute("isFollowing", isFollowing);

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("blog", blog);
        model.addAttribute("blogOwner", blogOwner);

        return "/view/blog/blog";
    }

    @GetMapping("/sort/likes/{id}")
    public String sortedByLikes(@PathVariable(name = "id") Long blogId, Model model, HttpServletRequest request) {
        Long userId = userService.getUserIdFromCookie(request);
        User blogOwner = userService.findUserById(blogService.getBlogById(blogId).getUser().getId());
        Blog blog = blogService.getBlogById(blogId);
        List<Post> posts = postService.getPostsOrderByLikes(blog);
        Set<User> following = followService.getFollowsByFollower(blogOwner);
        Set<User> follower = followService.getFollowsByFollowing(blogOwner);
        User user = userService.findUserById(userId);
        boolean isFollowing = followService.isFollowing(following, user);

        model.addAttribute("follower", follower);
        model.addAttribute("following", following);
        model.addAttribute("isFollowing", isFollowing);

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("blog", blog);
        model.addAttribute("blogOwner", blogOwner);
        return "/view/blog/blog";
    }

    @GetMapping("/sort/views/{id}")
    public String sortedByViews(@PathVariable(name = "id") Long blogId, Model model, HttpServletRequest request) {
        Long userId = userService.getUserIdFromCookie(request);
        User blogOwner = userService.findUserById(blogService.getBlogById(blogId).getUser().getId());
        Blog blog = blogService.getBlogById(blogId);
        List<Post> posts = postService.getPostsOrderByView(blog);
        Set<User> following = followService.getFollowsByFollower(blogOwner);
        Set<User> follower = followService.getFollowsByFollowing(blogOwner);
        User user = userService.findUserById(userId);
        boolean isFollowing = followService.isFollowing(following, user);

        model.addAttribute("follower", follower);
        model.addAttribute("following", following);
        model.addAttribute("isFollowing", isFollowing);

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("blog", blog);
        model.addAttribute("blogOwner", blogOwner);
        return "/view/blog/blog";
    }

    @GetMapping("/series/{id}")
    public String filterBySeries(@PathVariable(name = "id") Long blogId, Model model, HttpServletRequest request) {
        Long userId = userService.getUserIdFromCookie(request);
        User blogOwner = userService.findUserById(blogService.getBlogById(blogId).getUser().getId());
        Blog blog = blogService.getBlogById(blogId);
        Set<Series> allSeries = seriesService.getSeriesByBlogId(blogId);
        List<Post> posts = new ArrayList<>();
        for (Series series : allSeries) {
            posts.add(series.getPost());
        }
        Set<User> following = followService.getFollowsByFollower(blogOwner);
        Set<User> follower = followService.getFollowsByFollowing(blogOwner);
        User user = userService.findUserById(userId);
        boolean isFollowing = followService.isFollowing(following, user);

        model.addAttribute("follower", follower);
        model.addAttribute("following", following);
        model.addAttribute("isFollowing", isFollowing);

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("blog", blog);
        model.addAttribute("blogOwner", blogOwner);
        return "/view/blog/blog";
    }

    @GetMapping("/sort/date")
    public String allPostSortedByDate() {

    }

//    @GetMapping("/tags/{id}")
//    public String filterByTags(@PathVariable(name = "id") Long blogId,Model model,HttpServletRequest request) {
//        Long userId = userService.getUserIdFromCookie(request);
//        User blogOwner = userService.findUserById(blogService.getBlogById(blogId).getUser().getId());
//        Blog blog = blogService.getBlogById(blogId);
//        List<Post> posts = postService.;
//        model.addAttribute("user", userService.findUserById(userId));
//        model.addAttribute("posts", posts);
//        model.addAttribute("blog", blog);
//        model.addAttribute("blogOwner", blogOwner);
//
//        return "/view/blog/blog";
//    }
}