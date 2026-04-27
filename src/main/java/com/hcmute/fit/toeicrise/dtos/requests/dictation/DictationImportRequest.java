package com.hcmute.fit.toeicrise.dtos.requests.dictation;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DictationImportRequest {
    @NotNull(message = MessageConstant.TEST_ID_NOT_NULL)
    Long testId;

    @NotNull(message = MessageConstant.PART_ID_NOT_NULL)
    @Min(value = 1, message = MessageConstant.PART_ID_MIN)
    @Max(value = 7, message = MessageConstant.PART_ID_MAX)
    Long partId;

    @NotEmpty(message = MessageConstant.DICTATION_TRANSCRIPT_LIST_NOT_EMPTY)
    @Valid
    List<DictationTranscriptRequest> transcripts;
}
