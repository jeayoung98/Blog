package org.example.blog.domain.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.blog.domain.blog.Blog;

@Entity
@Table(name = "Series")
@Getter @Setter
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "series_id")
    private Long seriesId;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;
}
