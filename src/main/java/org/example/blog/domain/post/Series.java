package org.example.blog.domain.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.blog.domain.blog.Blog;

@Entity
@Table(name = "Series")
@Getter
@Setter
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "series_id")
    private Long seriesId;

    private String title;

    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @OneToOne
    @JoinColumn(name = "post_id", nullable = true)
    private Post post;
}
