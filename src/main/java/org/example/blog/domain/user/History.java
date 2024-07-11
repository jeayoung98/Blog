package org.example.blog.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.blog.domain.post.Post;

import java.time.LocalDate;

@Entity
@Table(name = "Histories")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;

    @Column(name = "view_day", nullable = false)
    private LocalDate viewDay;

    @PrePersist
    public void prePersist() {
        this.viewDay = LocalDate.now();
    }
}
