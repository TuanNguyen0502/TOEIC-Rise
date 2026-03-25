package com.hcmute.fit.toeicrise.dtos.responses.blog.category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogCategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private Integer numberOfPosts;
    private Boolean isActive;
}
