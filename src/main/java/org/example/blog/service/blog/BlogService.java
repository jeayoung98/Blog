package org.example.blog.service.blog;

import lombok.RequiredArgsConstructor;
import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.user.User;
import org.example.blog.repository.blog.BlogRepository;
import org.example.blog.repository.post.PostRepository;
import org.example.blog.repository.user.UserRepository;
import org.example.blog.service.blog.blogInterface.BlogInterface;
import org.example.blog.service.post.PostService;
import org.example.blog.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService implements BlogInterface {
    private final BlogRepository blogRepository;
    private final UserService userService;

    @Transactional
    public Long createBlog(String title, Long userId) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new IllegalStateException("사용자를 찾을 수 없습니다.");
        }

        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setUser(user);
        blog = blogRepository.save(blog);
        return blog.getBlogId();
    }

    public Blog findBlogByUserId(Long id) {
        if (getBlogByUserId(id) == null) {
            return null;
        }
        return blogRepository.findByUserId(id);
    }

    public Long getBlogId(Long id) {
        if (findBlogByUserId(id) == null) {
            return null;
        }
        return findBlogByUserId(id).getBlogId();
    }

    public Optional<Blog> getBlogByUserId(Long id) {
        return blogRepository.getBlogByUserId(id);
    }

    public Blog getBlogById(Long blogId) {
        return blogRepository.findById(blogId).orElseThrow(() -> new IllegalArgumentException("블로그가 없습니다"));
    }

    public void sortedPosts(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElse(null);
        List<Post> list = blog.getPosts().stream()
                .sorted((post1, post2) -> post2.getTime().compareTo(post1.getTime()))
                .toList();
        blog.setPosts(list);
    }

    public void saveBlog(Blog blog) {
        blogRepository.save(blog);
    }

    public void deleteBlog(Long userId) {
        blogRepository.deleteByUserId(userId);
    }




}