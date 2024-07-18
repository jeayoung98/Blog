package org.example.blog.domain.blog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.blog.domain.post.Post;
import org.example.blog.domain.post.Series;
import org.example.blog.domain.user.User;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Blogs")
@Getter
@Setter
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id")
    private Long blogId;

    @Column(nullable = false)
    private String title;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL)
    private Set<Series> series;
}
