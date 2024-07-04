package org.example.blog.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.blog.service.blog.BlogService;
import org.example.blog.service.post.FileStorageService;
import org.example.blog.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final BlogService blogService;

    private final FileStorageService fileStorageService;

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
            // 수정해야됨 이미지 업로드부분
//            String uploadedFileUrl = "";
//            if (profileImage != null && !profileImage.isEmpty()) {
//                uploadedFileUrl = "URL_TO_UPLOADED_IMAGE";
//            }

            String uploadedFileUrl = fileStorageService.storeFile(profileImage);


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
    public String showLoginForm(Model model) {

        return "/view/user/login";
    }

    @GetMapping("/welcome")
    public String showWelcome() {
        return "/view/welcome";
    }
}
