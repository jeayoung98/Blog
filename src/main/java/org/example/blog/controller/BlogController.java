package org.example.blog.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.blog.domain.User;
import org.example.blog.service.BlogService;
import org.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/blogs")
public class BlogController {
    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/create")
    public String createBlogForm(HttpServletRequest request,
                                 Model model) {
        Long id = userService.getUserIdFromCookie(request);
        if (id != null) {
            User user = userService.findUserById(id);
            if (user != null) {
                model.addAttribute("userId", id);
                return "/view/createBlog";
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
            return "redirect:/api/blogs/" + blogId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/blogs/create";
        }
    }

}
