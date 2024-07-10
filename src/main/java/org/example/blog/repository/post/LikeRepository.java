package org.example.blog.repository.post;

import org.example.blog.domain.post.Like;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByUserAndPost(User user, Post post);

    void deleteByPostAndUser(Post post, User user);

    boolean existsByPostAndUser(Post post,User user);
}
