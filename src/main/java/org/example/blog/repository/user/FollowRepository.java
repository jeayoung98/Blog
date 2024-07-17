package org.example.blog.repository.user;

import org.example.blog.domain.user.Follow;
import org.example.blog.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow,Long> {
    List<Follow> findFollowsByFollower(User user);
    List<Follow> findFollowsByFollowing(User user);
    Follow findByFollowerAndFollowing(User user, User currentUser);
    void deleteByFollowId(Optional<Follow> followId);
}
