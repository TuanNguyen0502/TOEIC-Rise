package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.commons.utils.StringListJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "questions")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question extends BaseEntity {
    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Convert(converter = StringListJsonConverter.class)
    @Column(columnDefinition = "json")
    private List<String> options;

    @Column(name = "correct_option", nullable = false, length = 1)
    private String correctOption;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_group_id")
    private QuestionGroup questionGroup;
}
