package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class QuestionGroupUpdateRequest {
    @Size(max = Constant.QUESTION_GROUP_AUDIO_MAX_SIZE, message = MessageConstant.QUESTION_GROUP_AUDIO_SIZE_EXCEEDED)
    private MultipartFile audio;

    @Size(max = Constant.QUESTION_GROUP_IMAGE_MAX_SIZE, message = MessageConstant.QUESTION_GROUP_IMAGE_SIZE_EXCEEDED)
    private MultipartFile image;

    private String passage;

    @NotBlank(message = MessageConstant.QUESTION_GROUP_TRANSCRIPT_NOT_BLANK)
    @NotNull(message = MessageConstant.QUESTION_GROUP_TRANSCRIPT_NOT_NULL)
    private String transcript;
}
