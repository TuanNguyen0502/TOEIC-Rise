package com.hcmute.fit.toeicrise.dtos.responses.blog.post;

import com.hcmute.fit.toeicrise.models.enums.EBlogPostStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogPostDetailForStaffResponse {
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String thumbnailUrl;
    private EBlogPostStatus status;
    private Integer views;
    private String createdAt;
    private String updatedAt;
    // Author information
    private Long authorId;
    private String authorName;
    private String authorEmail;
    // Blog category information
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private Boolean categoryIsActive;
}
