package com.hcmute.fit.toeicrise.dtos.requests.chatbot;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostSummaryRequest {
    @NotBlank(message = MessageConstant.BLOG_POST_TITLE_NOT_BLANK)
    @Pattern(regexp = Constant.BLOG_POST_TITLE_PATTERN, message = MessageConstant.BLOG_POST_TITLE_INVALID)
    private String title;

    @NotBlank(message = MessageConstant.BLOG_POST_CONTENT_NOT_BLANK)
    @Size(min = 50, message = MessageConstant.BLOG_POST_CONTENT_INVALID)
    private String content;
}
