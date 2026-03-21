package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.blog.BlogCategoryDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.BlogCategoryResponse;
import com.hcmute.fit.toeicrise.models.entities.BlogCategory;
import org.mapstruct.Mapper;

import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface BlogCategoryMapper {
    BlogCategoryResponse toBlogCategoryResponse(BlogCategory blogCategory);

    default BlogCategoryDetailResponse toBlogCategoryDetailResponse(BlogCategory blogCategory) {
        return BlogCategoryDetailResponse.builder()
                .id(blogCategory.getId())
                .name(blogCategory.getName())
                .slug(blogCategory.getSlug())
                .isActive(blogCategory.getIsActive())
                .createdAt(blogCategory.getCreatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                .updatedAt(blogCategory.getUpdatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                .build();
    }
}
