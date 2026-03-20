package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;

public interface IBlogPostService {
    PageResponse getBlogPostsByCategory(String category, int page, int size);
}
