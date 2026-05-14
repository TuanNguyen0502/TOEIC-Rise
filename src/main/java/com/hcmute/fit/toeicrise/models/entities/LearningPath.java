package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.models.enums.ETestType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "learning_paths", uniqueConstraints = {
        @UniqueConstraint(name = "uk_learning_paths_slug", columnNames = "slug")
})
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPath extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "test_type", nullable = false)
    private ETestType testType;

    @OneToMany(mappedBy = "learningPath", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    @ToString.Exclude
    private List<Lesson> lessons = new ArrayList<>();
}
