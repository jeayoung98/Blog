package org.example.blog.service.post.postInterface;

import org.example.blog.domain.post.Tag;

import java.util.Optional;

public interface TagInterface {
    Optional<Tag> getTagByName(String name);

    Tag saveTag(Tag tag);
}
