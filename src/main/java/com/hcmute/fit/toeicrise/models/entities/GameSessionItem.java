package com.hcmute.fit.toeicrise.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_session_items")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameSessionItem extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "game_session_id", nullable = false)
    private GameSession gameSession;

    @ManyToOne(optional = false)
    @JoinColumn(name = "flashcard_item_id", nullable = false)
    private FlashcardItem flashcardItem;

    @Column(name = "is_correct")
    private Boolean isCorrect;
}
