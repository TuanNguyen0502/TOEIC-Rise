package com.hcmute.fit.toeicrise.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_lesson_progress", uniqueConstraints = {
        @UniqueConstraint(name = "uc_ulp_user_lesson", columnNames = {"user_id", "lesson_id"})
})
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLessonProgress extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @ToString.Exclude
    private Lesson lesson;

    @Column(name = "progress_percentage", nullable = false, precision = 5, scale = 2)
    private Double progressPercentage;

    @Column(name = "last_watched_time_ms", nullable = false)
    private Long lastWatchedTimeMs;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;
}
