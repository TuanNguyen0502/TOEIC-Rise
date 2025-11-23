package com.hcmute.fit.toeicrise.dtos.responses.chatbot;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestSuggestionResponse {
    private String testName;
    private Long testId;
    private String reason;
}
