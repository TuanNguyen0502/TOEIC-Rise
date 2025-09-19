package com.hcmute.fit.toeicrise.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "questions_tags")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionTag {
    @EmbeddedId
    private QuestionTagId id;

    @ManyToOne
    @MapsId("questionId")
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
