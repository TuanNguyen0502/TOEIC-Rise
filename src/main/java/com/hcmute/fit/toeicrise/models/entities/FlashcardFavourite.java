package com.hcmute.fit.toeicrise.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flashcard_favourites",
        uniqueConstraints = @UniqueConstraint(
                name = "uc_user_flashcard",
                columnNames = {"user_id", "flashcard_id"}
        ))
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardFavourite extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Flashcard flashcard;
}
