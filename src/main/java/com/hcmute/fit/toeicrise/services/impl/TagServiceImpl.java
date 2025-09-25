package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.models.entities.Tag;
import com.hcmute.fit.toeicrise.repositories.TagRepository;
import com.hcmute.fit.toeicrise.services.interfaces.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements ITagService {
    private final TagRepository tagRepository;

    @Override
    public List<Tag> getTagsFromString(String tagsString) {
        List<Tag> tags = new ArrayList<>();
        if (!StringUtils.hasText(tagsString)) {
            return tags;
        }
        String[] tagNames = tagsString.split(";");
        for (String tagName : tagNames) {
            if (StringUtils.hasText(tagName)) {
                Tag tag = findOrCreateTag(tagName.trim());
                tags.add(tag);
            }
        }
        return tags;
    }

    private Tag findOrCreateTag(String tagName) {
        return tagRepository.findByName(tagName)
                .orElseGet(() -> {
                    Tag newTag = Tag.builder()
                            .name(tagName)
                            .build();
                    return tagRepository.save(newTag);
                });
    }
}