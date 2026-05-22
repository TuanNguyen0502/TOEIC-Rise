package com.hcmute.fit.toeicrise.dtos.responses.blog.post;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogPostDetailForLearnerResponse {
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String thumbnailUrl;
    private String authorName;
    private String categoryName;
    private String categorySlug;
    private Integer views;
    private String updatedAt;
}
