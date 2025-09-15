package com.hcmute.fit.toeicrise.models.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class QuestionTagId implements Serializable {
    private Long questionId;
    private Long tagId;

    public QuestionTagId() {}
    public QuestionTagId(Long questionId, Long tagId) {
        this.questionId = questionId;
        this.tagId = tagId;
    }

    // equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionTagId that)) return false;
        return Objects.equals(questionId, that.questionId) &&
                Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, tagId);
    }
}
