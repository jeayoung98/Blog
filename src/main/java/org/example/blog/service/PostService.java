package org.example.blog.service;

import org.example.blog.domain.Blog;
import org.example.blog.domain.Image;
import org.example.blog.domain.Post;
import org.example.blog.domain.Tag;
import org.example.blog.repository.BlogRepository;
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
    private BlogRepository blogRepository;

    @Autowired
    private TagRepository tagRepository;

    @Transactional
    public void createPost(Blog blog, String title, String content, String tags, List<Image> images) {
        Post post = new Post();
        post.setBlog(blog);
        post.setTitle(title);
        post.setContent(content);
        post.setTags(parseTags(tags));
        for (Image image : images) {
            image.setPost(post);
        }
        post.setImages(images);
        postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByPostIdAsc();
    }

    public void deletePostById(Long id) {
        postRepository.deleteById(id);
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
