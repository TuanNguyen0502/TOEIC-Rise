package com.hcmute.fit.toeicrise.dtos.responses.tag;

public interface TagStatisticsProjection {
    Long getId();

    String getName();

    Long getTotalQuestions();

    Long getTotalAnswers();

    Double getCorrectionRate();
}
