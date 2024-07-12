package org.example.blog.repository.blog;

import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Blog findByUser(User user);

    Blog findByUserId(Long userid);

    Optional<Blog> getBlogByUserId(Long userid);

    void deleteByUserId(Long userId);
}
