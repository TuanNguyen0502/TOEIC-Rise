package com.hcmute.fit.toeicrise.dtos.requests.comment;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditCommentRequest(
        @NotBlank(message = MessageConstant.COMMENT_NOT_BLANK)
        @Size(max = Constant.MAX_COMMENT_LENGTH, message = MessageConstant.COMMENT_TOO_LONG)
        String content
) {
}
