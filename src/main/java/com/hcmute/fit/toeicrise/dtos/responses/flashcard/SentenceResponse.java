package com.hcmute.fit.toeicrise.dtos.responses.flashcard;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SentenceResponse {
    private int score;
    private String suggestion;
    private List<String> improvement;
    private String remark;
}
