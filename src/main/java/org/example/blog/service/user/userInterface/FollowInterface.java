package org.example.blog.service.user.userInterface;

import org.example.blog.domain.user.Follow;
import org.example.blog.domain.user.User;

import java.util.List;
import java.util.Set;

public interface FollowInterface {
    void saveFollow(Follow follow);
    void setFollow(User user , User currentUser);
    List<User> getFollowsByFollower(User user);
    List<User> getFollowsByFollowing(User user);

    Follow getFollowByFollowerAndFollowing(User user, User currentUser);
    boolean isFollowing(List<User> following,User user);

}
