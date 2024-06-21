package org.example.blog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Follow")
@Setter @Getter
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long followId;

    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;
}
