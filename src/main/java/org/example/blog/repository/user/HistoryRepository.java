package org.example.blog.repository.user;

import org.example.blog.domain.post.Post;
import org.example.blog.domain.user.History;
import org.example.blog.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History,Long> {
    History findHistoryByUserIdAndPostPostId(Long userId, Long postId);

    List<History> findByUserIdOrderByViewDayAsc(Long userId);

    History findByUserId(Long userId);
}
