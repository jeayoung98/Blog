package org.example.blog.domain.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Tags")
@Getter
@Setter
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tags_id")
    private Long TagId;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<Post> posts;
}
