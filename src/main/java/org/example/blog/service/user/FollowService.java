package org.example.blog.service.user;

import lombok.RequiredArgsConstructor;
import org.example.blog.domain.user.Follow;
import org.example.blog.domain.user.User;
import org.example.blog.repository.user.FollowRepository;
import org.example.blog.service.user.userInterface.FollowInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
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
    public Set<User> getFollowsByFollower(User user) {
        List<Follow> follower = followRepository.findFollowsByFollower(user);
        Set<User> followeeUsers = new HashSet<>();
        for (Follow follow : follower) {
            followeeUsers.add(follow.getFollowing());
        }
        return followeeUsers;
    }

    @Override
    public Set<User> getFollowsByFollowing(User user) {
        List<Follow> following = followRepository.findFollowsByFollowing(user);
        Set<User> followingUsers = new HashSet<>();
        for (Follow follow : following) {
            followingUsers.add(follow.getFollower());
        }

        return followingUsers;
    }

    @Override
    public Follow getFollowByFollowerAndFollowing(User user, User currentUser) {
        return followRepository.findByFollowerIdAndFollowingId(user.getId(),currentUser.getId());
    }

    @Override
    public boolean isFollowing(Set<User> following,User user) {
        return following.contains(user);
    }

    @Transactional
    public void deleteFollow(Follow follow) {

        followRepository.deleteByFollowId(followRepository.findById(follow.getFollowId()).get().getFollowId());
    }
}
