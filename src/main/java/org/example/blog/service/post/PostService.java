package org.example.blog.service.post;

import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.Image;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.post.PublishedType;
import org.example.blog.domain.post.Tag;
import org.example.blog.repository.blog.BlogRepository;
import org.example.blog.repository.post.PostRepository;
import org.example.blog.repository.post.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void createPost(Blog blog, String title, String content, String tags, List<Image> images, PublishedType published) {
        Post post = new Post();
        post.setBlog(blog);
        post.setTitle(title);
        post.setContent(content);
        post.setTags(parseTags(tags));
        post.setPublished(published);
        for (Image image : images) {
            image.setPost(post);
        }
        post.setImages(images);
        postRepository.save(post);
    }
    @Transactional
    public void savePost(Post post) {
        postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByPostIdAsc();
    }

    @Transactional
    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public List<Post> getAllPostByPublished(PublishedType publishedType){
        return postRepository.findAllByPublished(publishedType);
    }

    public List<Post> getDraftPostsByBlog(Long blogId) {
        return postRepository.findByBlogAndPublished(blogRepository.findById(blogId).orElseThrow(), PublishedType.DRAFT);
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
