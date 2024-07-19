package org.example.blog.service.post;

import lombok.RequiredArgsConstructor;
import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.Image;
import org.example.blog.domain.post.*;
import org.example.blog.domain.user.History;
import org.example.blog.repository.post.PostRepository;
import org.example.blog.service.blog.blogInterface.BlogInterface;
import org.example.blog.service.post.postInterface.PostInterface;
import org.example.blog.service.post.postInterface.SeriesInterface;
import org.example.blog.service.post.postInterface.TagInterface;
import org.example.blog.service.user.userInterface.HistoryInterface;
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
    private final SeriesInterface seriesService;


    @Transactional
    public void createPost(Blog blog, String title, String content,
                           List<Image> images, PublishedType published,boolean status,String series,String newSeries) {
        Post post = new Post();
        post.setBlog(blog);
        post.setTitle(title);

        post.setContent(content);
        post.setPublished(published);
        post.setStatus(status);
        for (Image image : images) {
            image.setPost(post);
        }
        post.setImages(images);


        if (newSeries != null && !newSeries.isEmpty()) {
            Series seriesNew = seriesService.createSeries(blog, post, newSeries);
            post.setSeries(seriesNew);
        } else {
            if (series.isEmpty() || series == null) {
                Series noTitleSeries = seriesService.createSeries(blog, post, null);
                post.setSeries(noTitleSeries);
            } else {
                Series existingSeries = seriesService.createSeries(blog, post, series);
                post.setSeries(existingSeries);
            }
        }

        postRepository.save(post);
    }

    @Transactional
    public void savePost(Post post) {
        postRepository.save(post);
    }

    public List<Post> getAllPosts(PublishedType publishedType,boolean status) {
        return postRepository.findPostsByPublishedAndStatusOrderByTimeAsc(publishedType, status);
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
        return postRepository.findByBlogAndPublishedOrderByTimeAsc(blogService.getBlogById(blogId), PublishedType.DRAFT);
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
            if (userId != 0L) {
                if (currentUserHistories.size() == 0) {
                    Post post = getPostById(postId);
                    post.setView(post.getView() + 1);
                    savePost(post);
                } else {
                    boolean flag = false;
                    for (History currentHistory : currentUserHistories) {
                        if (Objects.equals(currentHistory.getPost().getPostId(), postId) && Objects.equals(currentHistory.getViewDay(), LocalDate.now())) {
                            flag = true;
                        }
                    }
                    if (!flag) {
                        Post post=getPostById(postId);
                        post.setView(post.getView() + 1);
                        savePost(post);
                    }
                }
            }


        }
    }



    public void updatePost(Long postId,String title, String content, List<Image> images,
                           PublishedType published,boolean status,String series,String newSeries) {
        Post post = getPostById(postId);
        post.setTitle(title);
        post.setContent(content);
//        post.setTags(updatedPost.getTags());
        post.setPublished(published);
        post.setStatus(status);
        post.setImages(images);

        if (newSeries != null && !newSeries.isEmpty()) {
            Series seriesNew = seriesService.updateSeries(post.getBlog(), post, newSeries);
            post.setSeries(seriesNew);
        } else {
            if (series.isEmpty() || series == null) {
                Series noTitleSeries = seriesService.updateSeries(post.getBlog(), post, null);
                post.setSeries(noTitleSeries);
            } else {
                Series existingSeries = seriesService.updateSeries(post.getBlog(), post, series);
                post.setSeries(existingSeries);
            }
        }
        postRepository.save(post);
    }

    public Blog getBlogByPostId(Long postId) {
        Post post = getPostById(postId);
        if (post != null) {
            return post.getBlog();
        }
        return null;
    }

    public List<Post> getPostsOrderByLikes(Blog blog) {
        List<Post> posts = postRepository.findPostsByBlogAndPublished(blog,PublishedType.PUBLISHED);
        Collections.sort(posts, (post1, post2) -> Integer.compare(post2.getLikes().size(), post1.getLikes().size()));
        return posts;
    }

    public List<Post> getPostsByBlogOrderByTime(Blog blog) {
        List<Post> posts = postRepository.findPostsByBlogAndPublished(blog,PublishedType.PUBLISHED);
        Collections.sort(posts, (post1, post2) -> Long.compare(post2.getPostId(), post1.getPostId()));
        return posts;
    }

    public List<Post> getPostsOrderByView(Blog blog) {
        List<Post> posts = postRepository.findPostsByBlogAndPublished(blog,PublishedType.PUBLISHED);
        Collections.sort(posts, (post1, post2) -> Integer.compare(post2.getView(), post1.getView()));
        return posts;
    }

    public List<Post> getAllOrderByTime() {
        List<Post> posts = postRepository.findAllByPublished(PublishedType.PUBLISHED);
        Collections.sort(posts, (post1, post2) -> Long.compare(post2.getPostId(), post1.getPostId()));
        return posts;
    }

    public List<Post> getAllOrderByLikes() {
        List<Post> posts = postRepository.findAllByPublished(PublishedType.PUBLISHED);
        Collections.sort(posts, (post1, post2) -> Integer.compare(post2.getLikes().size(), post1.getLikes().size()));
        return posts;
    }

    public List<Post> getAllOrderByViews() {
        List<Post> posts = postRepository.findAllByPublished(PublishedType.PUBLISHED);
        Collections.sort(posts, (post1, post2) -> Integer.compare(post2.getView(), post1.getView()));
        return posts;
    }


//    public List<Post> getUserSeenPosts(User currentUser) {
//        List<History> histories = historyRepository.findHistoriesByUser(currentUser);
//        List<Post> posts = new ArrayList<>();
//        for (History history : histories) {
//            posts.add(history.getHistory())
//        }
//    }

}
