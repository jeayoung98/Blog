package org.example.blog.service;

import org.example.blog.domain.Blog;
import org.example.blog.domain.Post;
import org.example.blog.domain.User;
import org.example.blog.repository.BlogRepository;
import org.example.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Long createBlog(String title, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalStateException("사용자를 찾을 수 없습니다.");
        }

        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setUser(user);
        blog = blogRepository.save(blog);
        return blog.getBlogId();
    }

    public Blog getBlogByUserId(Long id) {
        return blogRepository.findByUserId(id);
    }

    public void sortedPosts(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElse(null);
        List<Post> list = blog.getPosts().stream()
                .sorted((post1, post2) -> post2.getTime().compareTo(post1.getTime()))
                .toList();
        blog.setPosts(list);
    }

}