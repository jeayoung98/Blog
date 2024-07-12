package org.example.blog.service.post.postInterface;

import org.example.blog.domain.Image;

public interface ImageInterface {
    Image getImageByPostId(Long postId);
}
