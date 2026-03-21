package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.models.enums.EBlogPostStatus;
import org.springframework.scheduling.annotation.Async;

public interface IBlogPostService {
    PageResponse getBlogPostsByCategoryForStaff(String category, String title, String slug, EBlogPostStatus status, int page, int size);

    @Async
    void achievedBlogPostsByCategory(Long categoryId);
}
