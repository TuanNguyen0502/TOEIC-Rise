package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.models.entities.Tag;

import java.util.List;

public interface ITagService {
    List<Tag> getTagsFromString(String tagsString);
}