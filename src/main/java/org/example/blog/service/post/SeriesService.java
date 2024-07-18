package org.example.blog.service.post;

import lombok.RequiredArgsConstructor;
import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.post.Series;
import org.example.blog.repository.post.SeriesRepository;
import org.example.blog.service.post.postInterface.SeriesInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SeriesService implements SeriesInterface {
    private final SeriesRepository seriesRepository;


    @Override
    public Set<Series> getSeriesByBlogId(Long blogId) {
        return seriesRepository.findSeriesByBlogBlogId(blogId);
    }

    @Transactional
    @Override
    public Series createSeries(Blog blog, Post post,String title) {
        Series series = new Series();
        series.setBlog(blog);
        series.setPost(post);
        series.setTitle(title);
        return seriesRepository.save(series);
    }

    @Transactional
    @Override
    public void deleteSeries(Long seriesId) {
        seriesRepository.deleteById(seriesId);
    }

    @Override
    public Series getSeriesById(Long seriesId) {
        return seriesRepository.findSeriesBySeriesId(seriesId);
    }

    @Override
    public Series getSeriesByTitle(String title) {
        return seriesRepository.findSeriesByTitle(title);
    }

    @Override
    public Series updateSeries(Blog blog, Post post, String title) {
        Series series = seriesRepository.findSeriesByPost(post);
        series.setTitle(title);
        return seriesRepository.save(series);
    }
}
