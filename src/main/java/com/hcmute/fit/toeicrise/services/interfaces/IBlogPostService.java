package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.models.enums.EBlogPostStatus;

public interface IBlogPostService {
    PageResponse getBlogPostsByCategory(String category, EBlogPostStatus status, int page, int size);
}
