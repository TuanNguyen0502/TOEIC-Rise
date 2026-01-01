package com.hcmute.fit.toeicrise.dtos.responses.learner;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class LearnerTestHistoryResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private List<String> parts;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Integer totalScore;
    private Integer timeSpent;

    public LearnerTestHistoryResponse(Long id, String name, LocalDateTime createdAt, List<String> parts, Integer correctAnswers, Integer totalQuestions, Integer totalScore, Integer timeSpent) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.parts = parts;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.totalScore = totalScore;
        this.timeSpent = timeSpent;
    }
}