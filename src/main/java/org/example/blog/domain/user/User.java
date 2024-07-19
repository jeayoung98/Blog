package org.example.blog.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.blog.domain.blog.Blog;
import org.example.blog.domain.post.Like;
import org.example.blog.domain.post.Post;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email_status", nullable = false)
    private boolean emailStatus = true;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = new Date();
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Blog blog;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Like> likes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<History> histories;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL,fetch = FetchType.LAZY) // 모든 CascadeType 적용
    private Set<Follow> follower;

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL,fetch = FetchType.LAZY) // 모든 CascadeType 적용
    private Set<Follow> following;


}