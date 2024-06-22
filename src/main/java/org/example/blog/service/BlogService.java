package org.example.blog.service;

import org.example.blog.domain.Blog;
import org.example.blog.domain.User;
import org.example.blog.repository.BlogRepository;
import org.example.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
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
}