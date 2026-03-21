package com.hcmute.fit.toeicrise.dtos.requests.blog.category;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogCategoryCreateRequest {
    @NotBlank(message = MessageConstant.BLOG_CATEGORY_NAME_NOT_BLANK)
    @Pattern(regexp = Constant.BLOG_NAME_PATTERN, message = MessageConstant.BLOG_CATEGORY_NAME_INVALID)
    private String name;

    @NotBlank(message = MessageConstant.BLOG_CATEGORY_SLUG_NOT_BLANK)
    @Pattern(regexp = Constant.BLOG_SLUG_PATTERN, message = MessageConstant.BLOG_CATEGORY_SLUG_INVALID)
    private String slug;
}
