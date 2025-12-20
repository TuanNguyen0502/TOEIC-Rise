package com.hcmute.fit.toeicrise.dtos.requests.question;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionGroupUpdateRequest {
    private MultipartFile audio;

    @URL(message = MessageConstant.QUESTION_GROUP_AUDIO_URL_INVALID)
    @Pattern(regexp = Constant.QUESTION_GROUP_AUDIO_URL_FORMAT, message = MessageConstant.QUESTION_GROUP_AUDIO_URL_FORMAT_INVALID)
    private String audioUrl;

    private MultipartFile image;

    @URL(message = MessageConstant.QUESTION_GROUP_IMAGE_URL_INVALID)
    @Pattern(regexp = Constant.QUESTION_GROUP_IMAGE_URL_FORMAT, message = MessageConstant.QUESTION_GROUP_IMAGE_URL_FORMAT_INVALID)
    private String imageUrl;

    private String passage;

    @NotBlank(message = MessageConstant.QUESTION_GROUP_TRANSCRIPT_NOT_BLANK)
    @NotNull(message = MessageConstant.QUESTION_GROUP_TRANSCRIPT_NOT_NULL)
    private String transcript;
}
