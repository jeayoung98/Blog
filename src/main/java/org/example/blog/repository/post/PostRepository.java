package org.example.blog.repository.post;

import org.example.blog.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Post findByPostId(Long postId);

    List<Post> findAllByOrderByPostIdAsc();
}
