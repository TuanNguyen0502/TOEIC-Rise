package com.hcmute.fit.toeicrise.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flashcard_items")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardItem extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Flashcard flashcard;

    @Column(name = "vocabulary", nullable = false)
    private String vocabulary;

    @Column(name = "definition", columnDefinition = "TEXT", nullable = false)
    private String definition;

    @Column(name = "audio_url", length = 512)
    private String audioUrl;

    @Column(name = "pronunciation")
    private String pronunciation;
}
