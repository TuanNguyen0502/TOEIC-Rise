package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_learning_paths", uniqueConstraints = {
        @UniqueConstraint(name = "uc_user_learning_path", columnNames = {"user_id", "learning_path_id"})
})
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLearningPath extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_path_id", nullable = false)
    @ToString.Exclude
    private LearningPath learningPath;

    @Column(name = "selected_at", nullable = false)
    private LocalDateTime selectedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private ELessonLevel level;
}
