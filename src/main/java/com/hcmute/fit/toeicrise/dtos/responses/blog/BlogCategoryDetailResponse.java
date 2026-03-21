package com.hcmute.fit.toeicrise.dtos.responses.blog;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogCategoryDetailResponse {
    private Long id;
    private String name;
    private String slug;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
}
