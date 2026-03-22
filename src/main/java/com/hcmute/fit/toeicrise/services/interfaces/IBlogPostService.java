package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.blog.post.BlogPostCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.blog.post.BlogPostImageRequest;
import com.hcmute.fit.toeicrise.dtos.requests.blog.post.BlogPostUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostDetailForLearnerResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostDetailForStaffResponse;
import com.hcmute.fit.toeicrise.models.enums.EBlogPostStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

public interface IBlogPostService {
    PageResponse getBlogPostsByCategory(String category, String title, String slug, EBlogPostStatus status, int page, int size);

    BlogPostDetailForStaffResponse getBlogPostDetailForStaff(Long blogPostId);

    BlogPostDetailForLearnerResponse getBlogPostDetailForLearner(Long blogPostId);

    @Async
    void achievedBlogPostsByCategory(Long categoryId);

    @Transactional
    void createBlogPost(String email, String categorySlug, BlogPostCreateRequest request);

    @Transactional
    void updateBlogPost(String email, Long blogPostId, BlogPostUpdateRequest request);

    @Transactional
    void changeStatus(String email, Long blogPostId, EBlogPostStatus status);

    String uploadImage(BlogPostImageRequest request);
}
