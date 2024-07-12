package org.example.blog.service.post;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.blog.domain.post.Like;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.user.User;
import org.example.blog.repository.post.LikeRepository;
import org.example.blog.service.post.postInterface.LikeInterface;
import org.example.blog.service.post.postInterface.PostInterface;
import org.example.blog.service.user.UserService;
import org.example.blog.service.user.userInterface.UserInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService implements LikeInterface {
    private final LikeRepository likeRepository;
    private final UserInterface userService;
    private final PostInterface postService;

    public boolean isLikedByCurrentUser(Post post,User user) {
        return likeRepository.existsByPostAndUser(post, user);
    }

    @Transactional
    public void addLike(Long postId, HttpServletRequest request) {
        Post post = postService.getPostById(postId);
        User user = userService.findUserById(userService.getUserIdFromCookie(request));
        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        likeRepository.save(like);
    }
    @Transactional
    public void removeLike(Long postId,HttpServletRequest request) {
        Post post = postService.getPostById(postId);
        User user = userService.findUserById(userService.getUserIdFromCookie(request));
        likeRepository.deleteByPostAndUser(post,user);
    }

    @Override
    public List<Like> findLikesByUser(User user) {
        return likeRepository.findLikesByUser(user);
    }

    public List<Like> findLikesByPost(Post post) {
        return likeRepository.findLikesByPost(post);
    }



}
