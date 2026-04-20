package com.hcmute.fit.toeicrise.dtos.responses.dictation;

import com.hcmute.fit.toeicrise.models.enums.EPart;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestDictationAvailableResponse {
    Long id;
    String name;
    List<EPart> availableParts;
}
