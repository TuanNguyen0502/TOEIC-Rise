package com.hcmute.fit.toeicrise.dtos.requests.blog.post;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.validators.annotations.ValidImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostCreateRequest {
    @NotBlank(message = MessageConstant.BLOG_POST_TITLE_NOT_BLANK)
    @Pattern(regexp = Constant.BLOG_POST_TITLE_PATTERN, message = MessageConstant.BLOG_POST_TITLE_INVALID)
    private String title;

    @NotBlank(message = MessageConstant.BLOG_POST_SLUG_NOT_BLANK)
    @Pattern(regexp = Constant.BLOG_POST_SLUG_PATTERN, message = MessageConstant.BLOG_POST_SLUG_INVALID)
    private String slug;

    @NotBlank(message = MessageConstant.BLOG_POST_SUMMARY_NOT_BLANK)
    @Pattern(regexp = Constant.BLOG_POST_SUMMARY_PATTERN, message = MessageConstant.BLOG_POST_SUMMARY_INVALID)
    private String summary;

    @NotBlank(message = MessageConstant.BLOG_POST_CONTENT_NOT_BLANK)
    @Pattern(regexp = Constant.BLOG_POST_CONTENT_PATTERN, message = MessageConstant.BLOG_POST_CONTENT_INVALID)
    private String content;

    @NotNull(message = MessageConstant.BLOG_POST_THUMBNAIL_NOT_NULL)
    @ValidImage
    private MultipartFile thumbnail;
}
