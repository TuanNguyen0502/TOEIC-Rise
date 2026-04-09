package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lessons", uniqueConstraints = {
        @UniqueConstraint(name = "uc_lessons_path_order", columnNames = {"learning_path_id", "order_index"})
})
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lesson extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_path_id", nullable = false)
    @ToString.Exclude
    private LearningPath learningPath;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "topic")
    private String topic;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 50)
    private ELessonLevel level;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
}
