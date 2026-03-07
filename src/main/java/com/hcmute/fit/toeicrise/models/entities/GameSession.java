package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.models.enums.EGame;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_sessions")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameSession extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private EGame game;

    @Column(name = "total_items", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalItems;

    @Column(name = "correct_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer correctCount;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    private List<GameSessionItem> gameSessionItems = new ArrayList<>();
}
