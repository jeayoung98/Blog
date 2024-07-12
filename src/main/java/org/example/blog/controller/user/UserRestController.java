package org.example.blog.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.blog.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @GetMapping("/users/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByName(username);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/users/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
