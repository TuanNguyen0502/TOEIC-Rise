package com.hcmute.fit.toeicrise.dtos.responses.dictation;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class TestSetDictationResponse {
    Long id;
    String name;
    private Integer totalTests;
    private Integer readyPartsCount;
    private Integer totalPartsCount;
}
