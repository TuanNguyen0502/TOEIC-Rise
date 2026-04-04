package com.hcmute.fit.toeicrise.dtos.requests.blog.category;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogCategoryUpdateRequest {
    @NotBlank(message = MessageConstant.BLOG_CATEGORY_NAME_NOT_BLANK)
    @Pattern(regexp = Constant.BLOG_CATEGORY_NAME_PATTERN, message = MessageConstant.BLOG_CATEGORY_NAME_INVALID)
    private String name;

    @NotBlank(message = MessageConstant.BLOG_CATEGORY_SLUG_NOT_BLANK)
    @Pattern(regexp = Constant.BLOG_CATEGORY_SLUG_PATTERN, message = MessageConstant.BLOG_CATEGORY_SLUG_INVALID)
    private String slug;

    @NotNull(message = MessageConstant.BLOG_CATEGORY_ACTIVE_NOT_NULL)
    private Boolean active;
}
