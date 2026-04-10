package com.hcmute.fit.toeicrise.dtos.requests.dictation;

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
public class DictationTranscriptUpdateRequest {
    @NotNull(message = "Dictation Transcript ID is required")
    Long id;
    String questionText;
    List<String> options;
    String passageText;
}
