package org.example.blog.service.post;

import lombok.RequiredArgsConstructor;
import org.example.blog.domain.Image;
import org.example.blog.repository.ImageRepository;
import org.example.blog.service.post.postInterface.ImageInterface;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService implements ImageInterface {
    private final ImageRepository imageRepository;

    public Image getImageByPostId(Long postId) {
        return imageRepository.findByPostPostId(postId);
    }
}
