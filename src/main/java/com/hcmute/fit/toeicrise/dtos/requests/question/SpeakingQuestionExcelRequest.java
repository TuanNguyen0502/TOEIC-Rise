package com.hcmute.fit.toeicrise.dtos.requests.question;

import com.hcmute.fit.toeicrise.validators.annotations.ValidQuestionSpeakingByPart;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ValidQuestionSpeakingByPart
public class SpeakingQuestionExcelRequest {
    private Integer partNumber;
    private String questionGroupId;
    private Integer numberOfQuestions;
    private String passageText;
    private String question;
    private String imageUrl;
    private Integer indexRow;
}