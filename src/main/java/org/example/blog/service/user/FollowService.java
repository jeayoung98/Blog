package org.example.blog.service.user;

import lombok.RequiredArgsConstructor;
import org.example.blog.domain.user.Follow;
import org.example.blog.domain.user.User;
import org.example.blog.repository.user.FollowRepository;
import org.example.blog.service.user.userInterface.FollowInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FollowService implements FollowInterface {
    private final FollowRepository followRepository;

    @Transactional
    @Override
    public void saveFollow(Follow follow) {
        followRepository.save(follow);
    }

    @Transactional
    @Override
    public void setFollow(User user , User currentUser) {
        Follow follow = new Follow();

        follow.setFollower(user);
        follow.setFollowing(currentUser);

        saveFollow(follow);
    }
    @Override
    public List<User> getFollowsByFollower(User user) {
        List<Follow> follower = followRepository.findFollowsByFollower(user);
        List<User> followeeUsers = new ArrayList<>();
        for (Follow follow : follower) {
            followeeUsers.add(follow.getFollowing());
        }
        return followeeUsers;
    }

    @Override
    public List<User> getFollowsByFollowing(User user) {
        List<Follow> following = followRepository.findFollowsByFollowing(user);
        List<User> followingUsers = new ArrayList<>();
        for (Follow follow : following) {
            followingUsers.add(follow.getFollower());
        }

        return followingUsers;
    }

    @Override
    public Follow getFollowByFollowerAndFollowing(User user, User currentUser) {
        return followRepository.findByFollowerAndFollowing(user,currentUser);
    }

    @Override
    public boolean isFollowing(List<User> following,User user) {
        return following.contains(user);
    }

    public void deleteFollow(Follow follow) {

        followRepository.deleteByFollowId(followRepository.findById(follow.getFollowId()));
    }
}
