package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.models.entities.Tag;

import java.util.Set;

public interface ITagService {
    Set<Tag> getTagsFromString(String tagsString);
}