package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.commons.utils.StringListJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(name = "explanations", columnDefinition = "TEXT")
    private String explanation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_group_id")
    private QuestionGroup questionGroup;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "questions_tags",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
}
