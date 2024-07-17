package org.example.blog.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.blog.domain.user.Follow;
import org.example.blog.domain.user.User;
import org.example.blog.service.blog.BlogService;
import org.example.blog.service.user.FollowService;
import org.example.blog.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserRestController {
    private final BlogService blogService;
    private final UserService userService;
    private final FollowService followService;

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

    @DeleteMapping("/users/delete/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        try {
            userService.deleteUser(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/users/follow/{userId}")
    public void followUser(@PathVariable("userId") Long userId,HttpServletRequest request) {
        Long currentUserId = userService.getUserIdFromCookie(request);
        User currentUser = userService.findUserById(currentUserId);
        User user = userService.findUserById(userId);

        followService.setFollow(user, currentUser);
    }

    @PostMapping("/users/unfollow/{userId}")
    public void unfollowUser(@PathVariable("userId") Long userId,HttpServletRequest request) {
        Long currentUserId = userService.getUserIdFromCookie(request);
        User currentUser = userService.findUserById(currentUserId);
        User user = userService.findUserById(userId);
        Follow follow = followService.getFollowByFollowerAndFollowing(user, currentUser);
        followService.deleteFollow(follow);
    }
}
