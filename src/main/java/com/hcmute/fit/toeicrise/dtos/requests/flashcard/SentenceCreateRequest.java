package com.hcmute.fit.toeicrise.dtos.requests.flashcard;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SentenceCreateRequest {
    @NotBlank(message = MessageConstant.SENTENCE_NOT_BLANK)
    @NotEmpty(message = MessageConstant.SENTENCE_NOT_EMPTY)
    private String sentence;

    @NotBlank(message = MessageConstant.KEYWORD_NOT_BLANK)
    @NotEmpty(message = MessageConstant.KEYWORD_NOT_EMPTY)
    private String keyword;
}
