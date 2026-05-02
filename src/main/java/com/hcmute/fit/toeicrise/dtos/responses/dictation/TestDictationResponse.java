package com.hcmute.fit.toeicrise.dtos.responses.dictation;

import com.hcmute.fit.toeicrise.models.enums.EPart;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class TestDictationResponse {
    Long id;
    String name;
    List<EPart> readyParts;
}
