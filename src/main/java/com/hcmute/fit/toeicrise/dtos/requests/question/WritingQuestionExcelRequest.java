package com.hcmute.fit.toeicrise.dtos.requests.question;

import com.hcmute.fit.toeicrise.validators.annotations.ValidQuestionWritingByPart;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ValidQuestionWritingByPart
public class WritingQuestionExcelRequest {
    private Integer partNumber;
    private String questionGroupId;
    private Integer numberOfQuestions;
    private String passageText;
    private String imageUrl;
    private Integer indexRow;
}