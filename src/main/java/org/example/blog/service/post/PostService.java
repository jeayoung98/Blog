package org.example.blog.service.post;

import lombok.RequiredArgsConstructor;
import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.Image;
import org.example.blog.domain.post.Like;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.post.PublishedType;
import org.example.blog.domain.post.Tag;
import org.example.blog.domain.user.User;
import org.example.blog.repository.blog.BlogRepository;
import org.example.blog.repository.post.PostRepository;
import org.example.blog.repository.post.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BlogRepository blogRepository;
    private final TagRepository tagRepository;
    private final LikeService likeService;



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

    @Transactional
    public List<Post> getLikesPosts(User user) {
        List<Like> likes = likeService.findLikesByUser(user);
        List<Post> posts = new ArrayList<>();
        for (Like like : likes) {
            posts.add(getPostById(like.getPost().getPostId()));
        }
        return posts;
    }

}
