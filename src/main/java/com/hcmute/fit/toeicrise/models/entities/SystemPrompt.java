package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.models.enums.ESystemPromptFeatureType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_prompts")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemPrompt extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "feature_type", nullable = false)
    private ESystemPromptFeatureType featureType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer version;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
