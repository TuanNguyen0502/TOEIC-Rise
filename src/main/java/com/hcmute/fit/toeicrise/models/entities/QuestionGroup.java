package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.validators.annotations.QuestionGroupValidator;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_groups")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@QuestionGroupValidator
public class QuestionGroup extends BaseEntity {
    @Column(name = "audio_url", columnDefinition = "VARCHAR(255)")
    private String audioUrl;

    @Column(name = "image_url", columnDefinition = "VARCHAR(255)")
    private String imageUrl;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "passage", columnDefinition = "TEXT")
    private String passage;

    @Column(name = "transcript", columnDefinition = "TEXT")
    private String transcript;

    @ManyToOne(optional = false)
    @JoinColumn(name = "test_id")
    private Test test;

    @ManyToOne(optional = false)
    @JoinColumn(name = "part_id")
    private Part part;
}
