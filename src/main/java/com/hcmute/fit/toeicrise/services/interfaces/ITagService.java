package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.models.entities.Tag;

import java.util.List;

public interface ITagService {
    List<Tag> getTagsFromString(String tagsString);
    PageResponse getAllTags(int page, int pageSize, String tagsName);
}