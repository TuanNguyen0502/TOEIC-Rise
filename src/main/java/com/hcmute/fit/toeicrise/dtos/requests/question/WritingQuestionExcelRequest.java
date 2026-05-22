package com.hcmute.fit.toeicrise.dtos.requests.question;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WritingQuestionExcelRequest {
    private Integer partNumber;
    private String questionGroupId;
    private Integer numberOfQuestions;
    private String passageText;
    private String imageUrl;
}