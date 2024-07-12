package org.example.blog.service.post;

import lombok.RequiredArgsConstructor;
import org.example.blog.domain.post.Tag;
import org.example.blog.repository.post.TagRepository;
import org.example.blog.service.post.postInterface.TagInterface;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService implements TagInterface {
    private final TagRepository tagRepository;

    @Override
    public Optional<Tag> getTagByName(String tagName) {
        return tagRepository.findByName(tagName);
    }

    @Override
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

}
