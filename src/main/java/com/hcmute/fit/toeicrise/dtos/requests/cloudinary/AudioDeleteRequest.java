package com.hcmute.fit.toeicrise.dtos.requests.cloudinary;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AudioDeleteRequest {
    @NotBlank(message = MessageConstant.QUESTION_GROUP_AUDIO_URL_INVALID)
    @Pattern(regexp = Constant.QUESTION_GROUP_AUDIO_URL_FORMAT, message = MessageConstant.QUESTION_GROUP_AUDIO_URL_INVALID)
    private String audioUrl;
}
