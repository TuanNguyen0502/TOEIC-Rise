package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.blog.BlogCategoryResponse;
import com.hcmute.fit.toeicrise.models.entities.BlogCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BlogCategoryMapper {
    BlogCategoryResponse toBlogCategoryResponse(BlogCategory blogCategory);
}
