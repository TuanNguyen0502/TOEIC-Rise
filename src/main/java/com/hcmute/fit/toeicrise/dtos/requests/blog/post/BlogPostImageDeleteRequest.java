package com.hcmute.fit.toeicrise.dtos.requests.blog.post;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostImageDeleteRequest {
    @NotBlank(message = MessageConstant.BLOG_POST_IMAGE_NOT_BLANK)
    private String imageUrl;
}
