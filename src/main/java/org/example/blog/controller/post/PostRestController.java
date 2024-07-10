package org.example.blog.controller.post;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.blog.service.post.LikeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostRestController {
    private final LikeService likeService;

    @PostMapping("/like/{postId}")
    public void likePost(@PathVariable(name = "postId") Long postId, @RequestParam(name = "like", required = false, defaultValue = "false") boolean like, HttpServletRequest request) {
        if (like) {
            likeService.addLike(postId,request);
        } else {
            likeService.removeLike(postId,request);
        }
    }
}
