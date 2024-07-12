package org.example.blog.service.post.postInterface;

import org.example.blog.domain.post.Like;
import org.example.blog.domain.user.User;

import java.util.List;

public interface LikeInterface {
    List<Like> findLikesByUser(User user);
}
