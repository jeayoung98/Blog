package org.example.blog.service.post;

import lombok.RequiredArgsConstructor;
import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.Image;
import org.example.blog.domain.post.Like;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.post.PublishedType;
import org.example.blog.domain.post.Tag;
import org.example.blog.domain.user.History;
import org.example.blog.domain.user.User;
import org.example.blog.repository.blog.BlogRepository;
import org.example.blog.repository.post.PostRepository;
import org.example.blog.repository.post.TagRepository;
import org.example.blog.repository.user.HistoryRepository;
import org.example.blog.service.blog.BlogService;
import org.example.blog.service.blog.blogInterface.BlogInterface;
import org.example.blog.service.post.postInterface.LikeInterface;
import org.example.blog.service.post.postInterface.PostInterface;
import org.example.blog.service.post.postInterface.TagInterface;
import org.example.blog.service.user.userInterface.HistoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements PostInterface {
    private final PostRepository postRepository;
    private final BlogInterface blogService;
    private final TagInterface tagService;
    private final HistoryInterface historyService;



    @Transactional
    public void createPost(Blog blog, String title, String content, String tags, List<Image> images, PublishedType published,boolean status) {
        Post post = new Post();
        post.setBlog(blog);
        post.setTitle(title);

        post.setContent(content);
        post.setTags(parseTags(tags));
        post.setPublished(published);
        post.setStatus(status);
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

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public List<Post> getAllPostByPublished(PublishedType publishedType){
        return postRepository.findAllByPublished(publishedType);
    }

    public List<Post> getDraftPostsByBlog(Long blogId) {
        return postRepository.findByBlogAndPublished(blogService.getBlogById(blogId), PublishedType.DRAFT);
    }


    @Transactional
    public List<Tag> parseTags(String tags) {
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .map(tagName -> tagService.getTagByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagService.saveTag(newTag);
                        }))
                .collect(Collectors.toList());
    }

    public void postView(Long postId,Long userId) {
        if (getBlogByPostId(postId).getUser().getId() != userId) {
            List<History> currentUserHistories = historyService.getHistoryByUserId(userId);
            if (currentUserHistories.size() == 0) {
                Post post = getPostById(postId);
                post.setView(post.getView() + 1);
                savePost(post);
            } else {
                for (History currentHistory : currentUserHistories) {
                    if (currentHistory.getPost().getPostId() != postId && !Objects.equals(currentHistory.getViewDay(), LocalDate.now())) {
                        Post post=getPostById(postId);
                        post.setView(post.getView() + 1);
                        savePost(post);
                    }
                }
            }

        }
    }



    public void updatePost(Long postId,String title, String content, String tags, List<Image> images, PublishedType published,boolean status) {
        Post post = getPostById(postId);
        post.setTitle(title);
        post.setContent(content);
//        post.setTags(updatedPost.getTags());
        post.setPublished(published);
        post.setStatus(status);
        post.setImages(images);
        postRepository.save(post);
    }

    public Blog getBlogByPostId(Long postId) {
        Post post = getPostById(postId);
        if (post != null) {
            return post.getBlog();
        }
        return null;
    }


//    public List<Post> getUserSeenPosts(User user) {
//        List<History> histories = historyRepository.findHistoriesByUser(user);
//        List<Post> posts = new ArrayList<>();
//        for (History history : histories) {
//            posts.add(history.getHistory())
//        }
//    }

}
