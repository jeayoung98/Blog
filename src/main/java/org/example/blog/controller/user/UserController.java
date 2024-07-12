package org.example.blog.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.user.User;
import org.example.blog.service.blog.BlogService;
import org.example.blog.service.post.FileStorageService;
import org.example.blog.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final BlogService blogService;

    @GetMapping
    public String showUserOptions() {
        return "/view/user/users";
    }

    @GetMapping("/userregform")
    public String showRegistrationForm() {
        return "/view/user/register";
    }

    @PostMapping("/userreg")
    public String registerUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("username") String username,
            @RequestParam("name") String name,
            @RequestParam(name = "emailStatus", required = false, defaultValue = "false") boolean emailStatus,
            @RequestParam(name = "profileImage", required = false) MultipartFile profileImage,
            RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("name", name);
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("emailStatus", emailStatus);
            redirectAttributes.addFlashAttribute("profileImage", profileImage);
            if (profileImage != null && !profileImage.isEmpty()) {
                redirectAttributes.addFlashAttribute("profileImageName", profileImage.getOriginalFilename());
            }
            return "redirect:/userreg";
        }

        try {
            String uploadedFileUrl = "/upload/"+ fileStorageService.storeFile(profileImage);
            userService.createUser(email, password,username, name, uploadedFileUrl, emailStatus);
            return "redirect:/welcome";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/userreg";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/loginform")
    public String showLoginForm() {
        return "/view/user/login";
    }

    @GetMapping("/welcome")
    public String showWelcome() {
        return "/view/welcome";
    }

    @GetMapping("/mypage")
    public String showMyPage(Model model, HttpServletRequest request) {
        User user = userService.findUserById(userService.getUserIdFromCookie(request));
        if (user == null) {
            return "redirect:/login";
        }
        Blog blog = blogService.findBlogByUserId(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("blog", blog);
        return "view/user/mypage";
    }

    @PostMapping("/mypage/update")
    public String updateMyPage(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("emailNotifications") boolean emailNotifications,
            @RequestParam("profilePic") MultipartFile profilePic,
            @RequestParam("blogName") String blogName,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        User user = userService.findUserById(userService.getUserIdFromCookie(request));
        if (user == null) {
            return "redirect:/login";
        }

        try {
            user.setName(username);
            user.setEmail(email);
            user.setEmailStatus(emailNotifications);
            if (!profilePic.isEmpty()) {
                String profilePicPath = "/upload/"+fileStorageService.storeFile(profilePic);
                user.setProfileImage(profilePicPath);
            }
            userService.saveUser(user);

            Blog blog = blogService.findBlogByUserId(user.getId());
            blog.setTitle(blogName);
            blogService.saveBlog(blog);

            redirectAttributes.addFlashAttribute("success", "정보가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "정보 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "redirect:/mypage";
    }


}
