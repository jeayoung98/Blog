package org.example.blog.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.blog.domain.Blog;
import org.example.blog.domain.User;
import org.example.blog.service.BlogService;
import org.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private BlogService blogService;

    @GetMapping
    public String showUserOptions() {
        return "/view/users";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "/view/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("name") String name,
            @RequestParam(name = "emailStatus", required = false, defaultValue = "false") boolean emailStatus,
            @RequestParam(name = "profileImage", required = false) MultipartFile profileImage,
            RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("name", name);
            redirectAttributes.addFlashAttribute("emailStatus", emailStatus);
            redirectAttributes.addFlashAttribute("profileImage", profileImage);
            if (profileImage != null && !profileImage.isEmpty()) {
                redirectAttributes.addFlashAttribute("profileImageName", profileImage.getOriginalFilename());
            }
            return "redirect:/api/register";
        }

        try {
            // 수정해야됨 이미지 업로드부분
            String uploadedFileUrl = "";
            if (profileImage != null && !profileImage.isEmpty()) {

                uploadedFileUrl = "URL_TO_UPLOADED_IMAGE";
            }
            userService.createUser(email, password, name, uploadedFileUrl, emailStatus);
            return "redirect:/api/welcome";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/register";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            HttpServletResponse response,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        boolean isLogIn = userService.loginUser(email, password, response);
        if (isLogIn) {
            User user = userService.findByEmail(email);
            Blog blog = blogService.getBlogById(user.getId());

            if (blog != null) {
                model.addAttribute(blog);
                return "redirect:/api/blogs/"+blog.getBlogId();
            } else {
                return "redirect:/api/blogs/create";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 틀림");
            return "redirect:/api/login";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "/view/login";
    }

    @GetMapping("/welcome")
    public String showWelcome() {
        return "/view/welcome";
    }
}
