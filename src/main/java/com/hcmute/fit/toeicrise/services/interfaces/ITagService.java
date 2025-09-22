package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.models.entities.Tag;

public interface ITagService {
    Tag findOrCreateTag(String tagName);
}