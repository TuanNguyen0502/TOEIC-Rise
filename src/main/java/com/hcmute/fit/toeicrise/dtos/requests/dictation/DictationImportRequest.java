package com.hcmute.fit.toeicrise.dtos.requests.dictation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DictationImportRequest {
    @NotNull(message = "Test ID is required")
    Long testId;

    @NotNull(message = "Part ID is required")
    Long partId;

    @NotEmpty(message = "Transcripts list cannot be empty")
    @Valid
    List<DictationTranscriptRequest> transcripts;
}
