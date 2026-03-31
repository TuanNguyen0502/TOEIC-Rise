package com.hcmute.fit.toeicrise.dtos.requests.dictation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiDictationRequest {
    private Long questionGroupId;
    private String partName;
    private String transcript;
}
