package com.hcmute.fit.toeicrise.dtos.responses.blog;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogPostResponse {
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String thumbnailUrl;
    private String authorName;
    private Integer views;
    private String updatedAt;
}
