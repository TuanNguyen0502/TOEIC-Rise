package com.hcmute.fit.toeicrise.dtos.requests.blog.post;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.validators.annotations.ValidImage;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class BlogPostImageRequest {
    private String oldThumbnailUrl;

    @NotNull(message = MessageConstant.BLOG_POST_THUMBNAIL_NOT_NULL)
    @ValidImage
    private MultipartFile thumbnail;
}
