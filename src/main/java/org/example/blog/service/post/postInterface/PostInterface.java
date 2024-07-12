package org.example.blog.service.post.postInterface;

import org.example.blog.domain.post.Post;

public interface PostInterface {
    Post getPostById(Long postId);
}
