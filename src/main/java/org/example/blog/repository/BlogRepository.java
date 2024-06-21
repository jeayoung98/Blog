package org.example.blog.repository;

import org.example.blog.domain.Blog;
import org.example.blog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Blog findByUser(User user);

    boolean existsByUserId(Long userid);

    Blog findByUserId(Long userid);
}
