package org.example.blog.domain.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.blog.domain.Image;
import org.example.blog.domain.blog.Blog;

import java.util.*;

@Entity
@Table(name = "Posts")
@Getter @Setter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @Column(nullable = false)
    private Date time = new Date();

    @Column(nullable = false)
    private boolean status;

    @Column
    private int likes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublishedType published;

    @Column(nullable = false)
    private int view;

    @ManyToMany
    @JoinTable(
            name = "PostTags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Image> images = new ArrayList<>();

}
