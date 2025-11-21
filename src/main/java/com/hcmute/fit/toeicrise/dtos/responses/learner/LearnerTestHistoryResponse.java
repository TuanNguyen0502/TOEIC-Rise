package com.hcmute.fit.toeicrise.dtos.responses.learner;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class LearnerTestHistoryResponse {
    private Long id;
    private String name;
    private LocalDate createAt;
    private List<String> partNames;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Integer score;
    private Integer timeSpent;

    public LearnerTestHistoryResponse(Long id, String name, LocalDateTime createAt, List<String> partNames, Integer correctAnswers, Integer totalQuestions, Integer score, Integer timeSpent) {
        this.id = id;
        this.name = name;
        this.createAt = createAt.toLocalDate();
        this.partNames = partNames;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.score = score;
        this.timeSpent = timeSpent;
    }
}