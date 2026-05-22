package com.hcmute.fit.toeicrise.dtos.requests.usertest.speaking;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingAnswerSubmissionRequest {
    @NotNull(message = MessageConstant.QUESTION_ID_NOT_NULL)
    private Long questionId;

    private MultipartFile answerAudio;
}
