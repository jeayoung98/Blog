package org.example.blog.repository.post;

import org.example.blog.domain.post.Post;
import org.example.blog.domain.post.PublishedType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Post findByPostId(Long postId);

    List<Post> findAllByOrderByPostIdAsc();

    List<Post> findAllByPublished(PublishedType published);
//    List<Post> findAllByPublishedAndStatus(PublishedType published, boolean status);
}
