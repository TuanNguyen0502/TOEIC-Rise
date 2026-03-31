package com.hcmute.fit.toeicrise.dtos.responses.dictation;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictationGenerationResponse {
    private Long questionGroupId;
    private String questionText;
    private List<String> options;
    private String passageText;
}