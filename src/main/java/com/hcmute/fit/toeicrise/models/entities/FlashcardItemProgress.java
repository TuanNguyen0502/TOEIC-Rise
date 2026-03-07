package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.models.enums.ELevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "flashcard_item_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "flashcard_item_id"})
})
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlashcardItemProgress extends BaseEntity{
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "flashcard_item_id")
    private FlashcardItem flashcardItem;

    @Enumerated(EnumType.STRING)
    private ELevel level;

    @Column(name = "next_review_at")
    private LocalDateTime nextReviewAt;
}
