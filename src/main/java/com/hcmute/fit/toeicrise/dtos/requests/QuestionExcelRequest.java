package com.hcmute.fit.toeicrise.dtos.requests;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionExcelRequest {
    private Integer partNumber;
    private String questionGroupId;
    private Integer numberOfQuestions;
    private String passageText;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String audioUrl;
    private String imageUrl;
    private String explanation;
    private String transcript;
    private String tags;
}