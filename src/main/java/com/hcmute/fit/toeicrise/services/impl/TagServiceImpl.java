package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.models.entities.Tag;
import com.hcmute.fit.toeicrise.repositories.TagRepository;
import com.hcmute.fit.toeicrise.services.interfaces.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements ITagService {
    private final TagRepository tagRepository;

    @Override
    public Set<Tag> getTagsFromString(String tagsString) {
        Set<Tag> tags = new HashSet<>();
        if (!StringUtils.hasText(tagsString)) {
            return tags;
        }

        String[] tagNames = tagsString.split(";");
        for (String tagName : tagNames) {
            if (StringUtils.hasText(tagName)) {
                Tag tag = findOrCreateTag(tagName);
                tags.add(tag);
            }
        }

        return tags;
    }

    private Tag findOrCreateTag(String tagName) {
        Tag tag = tagRepository.findByName(tagName).orElse(null);
        if (tag == null) {
            tag = new Tag();
            tag.setName(tagName);
            tagRepository.save(tag);
        }
        return tag;
    }
}