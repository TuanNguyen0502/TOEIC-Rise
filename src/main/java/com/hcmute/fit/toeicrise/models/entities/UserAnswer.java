package com.hcmute.fit.toeicrise.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_answers")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswer extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_test_id")
    private UserTest userTest;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "question_group_id", nullable = false)
    private Long questionGroupId;

    @Column(name = "answer", nullable = false, columnDefinition = "CHAR(1) NOT NULL")
    private String answer;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;
}
