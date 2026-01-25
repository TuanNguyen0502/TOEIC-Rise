package com.hcmute.fit.toeicrise.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "tags")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "number_of_user_answers")
    private Integer numberOfUserAnswers;

    @Column(name = "user_answers_correction_rate")
    private Float userAnswersCorrectionRate;

    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    private List<Question> questions = new ArrayList<>();
}