package org.example.blog.service.post.postInterface;

import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.post.Series;

import java.util.Set;

public interface SeriesInterface {
    Set<Series> getSeriesByBlogId(Long blogId);
    Series createSeries(Blog blog, Post post, String title);
    void deleteSeries(Long seriesId);
    Series getSeriesById(Long seriesId);
    Series getSeriesByTitle(String title);
    Series updateSeries(Blog blog, Post post, String title);
}
