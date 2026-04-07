package com.hcmute.fit.toeicrise.dtos.responses.dictation;

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
public class ListeningDictationResponse {
     String partName;
     List<QuestionGroupDictationResponse> questionGroups;
}
