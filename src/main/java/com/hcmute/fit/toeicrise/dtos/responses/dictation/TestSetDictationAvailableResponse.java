package com.hcmute.fit.toeicrise.dtos.responses.dictation;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestSetDictationAvailableResponse {
    Long id;
    String name;
    List<TestDictationAvailableResponse> readyTests;
}
