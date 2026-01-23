package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.TagByPartResponse;
import com.hcmute.fit.toeicrise.models.entities.Tag;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public interface ITagService {
    List<Tag> getTagsFromString(String tagsString, Function<String, Tag> resolver);

    PageResponse getAllTags(int page, int pageSize, String tagsName);

    PageResponse getAllTagsForDashboard(int page, int pageSize, String sortBy, String direction, String tagsName);

    List<TagByPartResponse> getTagsByPartId(Long partId);

    List<Tag> parseTagsAllowCreate(String tagsString);
    
    void checkExistsIds(Set<Long> tagIds);

    void createTagIfNotExists(String tagName);
}