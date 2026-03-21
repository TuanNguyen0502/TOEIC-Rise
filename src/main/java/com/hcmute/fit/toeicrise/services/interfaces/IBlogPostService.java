package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostDetailForLearnerResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostDetailForStaffResponse;
import com.hcmute.fit.toeicrise.models.enums.EBlogPostStatus;
import org.springframework.scheduling.annotation.Async;

public interface IBlogPostService {
    PageResponse getBlogPostsByCategory(String category, String title, String slug, EBlogPostStatus status, int page, int size);

    BlogPostDetailForStaffResponse getBlogPostDetailForStaff(Long blogPostId);

    BlogPostDetailForLearnerResponse getBlogPostDetailForLearner(Long blogPostId);

    @Async
    void achievedBlogPostsByCategory(Long categoryId);
}
