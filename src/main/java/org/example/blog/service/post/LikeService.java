package org.example.blog.service.post;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.post.Like;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.user.User;
import org.example.blog.repository.blog.BlogRepository;
import org.example.blog.repository.post.LikeRepository;
import org.example.blog.repository.post.PostRepository;
import org.example.blog.repository.user.UserRepository;
import org.example.blog.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final UserService userService;

    @Transactional
    public Like saveLike(Like like) {
        return likeRepository.save(like);
    }

    @Transactional
    public void delete(Like like) {
        likeRepository.delete(like);
    }

    public Like findByUserAndPost(User user, Post post) {
        return likeRepository.findByUserAndPost(user, post);
    }

    @Transactional
    public boolean isLikedByCurrentUser(Post post,User user) {
        return likeRepository.existsByPostAndUser(post, user);
    }

    @Transactional
    public void addLike(Long postId, HttpServletRequest request) {
        Post post = postRepository.findByPostId(postId);
        User user = userRepository.findById(userService.getUserIdFromCookie(request)).orElse(null);
        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        likeRepository.save(like);
    }
    @Transactional
    public void removeLike(Long postId,HttpServletRequest request) {
        Post post = postRepository.findByPostId(postId);
        User user = userRepository.findById(userService.getUserIdFromCookie(request)).orElse(null);
        likeRepository.deleteByPostAndUser(post,user);
    }

    public List<Like> findLikesByUser(User user) {
        return likeRepository.findLikesByUser(user);
    }

    public List<Like> findLikesByPost(Post post) {
        return likeRepository.findLikesByPost(post);
    }



}
