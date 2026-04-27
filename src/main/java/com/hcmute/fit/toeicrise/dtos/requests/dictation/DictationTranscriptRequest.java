package com.hcmute.fit.toeicrise.dtos.requests.dictation;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotNull;
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
public class DictationTranscriptRequest {
    @NotNull(message = MessageConstant.QUESTION_GROUP_ID_NOT_NULL)
    Long questionGroupId;
    String questionText;
    List<String> options;
    String passageText;
}
