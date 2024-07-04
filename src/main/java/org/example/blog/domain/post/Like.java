package org.example.blog.domain.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.blog.domain.user.User;

@Entity
@Table(name = "Likes")
@Setter @Getter
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long LikeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
