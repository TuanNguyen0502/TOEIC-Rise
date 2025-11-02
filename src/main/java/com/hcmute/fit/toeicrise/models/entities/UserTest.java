package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.commons.utils.StringListJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_tests")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTest extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "test_id")
    private Test test;

    // General fields

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "correct_answers")
    private Integer correctAnswers;

    @Column(name = "correct_percent")
    private Double correctPercent;

    @Column(name = "time_spent")
    private Integer timeSpent; // in seconds

    // Specific fields for practice mode

    @Convert(converter = StringListJsonConverter.class)
    @Column(columnDefinition = "json")
    private List<String> parts; // Part 1, Part 2, ...

    // Specific fields for exam mode

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "listening_score")
    private Integer listeningScore;

    @Column(name = "reading_score")
    private Integer readingScore;

    @Column(name = "listening_correct_answers")
    private Integer listeningCorrectAnswers;

    @Column(name = "reading_correct_answers")
    private Integer readingCorrectAnswers;

    @OneToMany(mappedBy = "userTest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserAnswer> userAnswers = new ArrayList<>();
}
