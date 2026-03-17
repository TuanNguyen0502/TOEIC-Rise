package com.hcmute.fit.toeicrise.dtos.requests.comment;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @NotBlank(message = MessageConstant.COMMENT_NOT_BLANK)
        @Size(max = Constant.MAX_COMMENT_LENGTH, message = MessageConstant.COMMENT_TOO_LONG)
        String content,

        @NotNull(message = MessageConstant.TEST_ID_NOT_NULL)
        Long testId,

        Long questionId, Long parentId) {
}