package com.hcmute.fit.toeicrise.dtos.responses.dictation;

import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class QuestionGroupDictationResponse {
    Long id;
    String audioUrl;
    String imageUrl;
    String passage;
    String transcript;
    Integer position;

    //For dictation
    String questionText;
    List<String> options;
    String passageText;
    List<QuestionResponse> questions;
}
