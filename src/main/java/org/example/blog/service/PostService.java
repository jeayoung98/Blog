package org.example.blog.service;

import org.example.blog.domain.Image;
import org.example.blog.domain.Post;
import org.example.blog.domain.Tag;
import org.example.blog.repository.ImageRepository;
import org.example.blog.repository.PostRepository;
import org.example.blog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private TagRepository tagRepository;

    @Transactional
    public void createPost(String title, String content, String tags, List<Image> imagePaths) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setTags(parseTags(tags));
        post.setImages(imagePaths);
        postRepository.save(post);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    @Transactional
    public List<Tag> parseTags(String tags) {
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        }))
                .collect(Collectors.toList());
    }

}
