package com.hcmute.fit.toeicrise.dtos.responses.chatbot;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotAnalysisResponse {
    private String overallSummary;
    private String strengths;
    private String weaknesses;
    private List<String> partAnalysis;
    private List<String> tagAnalysis;
    private String timingAnalysis;
    private String trendAnalysis;
    private String recommendations;
    private List<String> studyPlan;
}
