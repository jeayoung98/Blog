package org.example.blog.repository.post;

import org.example.blog.domain.post.Post;
import org.example.blog.domain.post.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
    Set<Series> findSeriesByBlogBlogId(Long blogId);

    Series findSeriesBySeriesId(Long seriesId);

    Series findSeriesByTitle(String title);

    Series findSeriesByPost(Post post);
}
