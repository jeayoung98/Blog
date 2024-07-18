package org.example.blog.repository.post;

import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.post.PublishedType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Post findByPostId(Long postId);

    List<Post> findPostsByPublishedAndStatusOrderByTimeAsc(PublishedType publishedType, boolean status);

    List<Post> findAllByPublished(PublishedType published);
//    List<Post> findAllByPublishedAndStatus(PublishedType published, boolean status);

    List<Post> findByBlogAndPublishedOrderByTimeAsc(Blog blog, PublishedType publishedType);

    List<Post> findPostsByPublishedAndBlogOrderByLikes(PublishedType publishedType, Blog blog);

    List<Post> findPostsByBlogOrderByView(Blog blog);

    List<Post> findPostsByBlogAndPublished(Blog blog, PublishedType publishedType);
}