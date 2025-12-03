package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.models.enums.EFlashcardAccessType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "flashcards")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Flashcard extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", nullable = false)
    private EFlashcardAccessType accessType;

    @Column(name = "favourite_count", columnDefinition = "INT DEFAULT 0")
    private Integer favouriteCount;

    @OneToMany(mappedBy = "flashcard", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<FlashcardItem> flashcardItems;

    @OneToMany(mappedBy = "flashcard", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<FlashcardFavourite> favourites;
}
