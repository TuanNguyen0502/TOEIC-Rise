package com.hcmute.fit.toeicrise.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_titles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatTitle {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "conversation_id", nullable = false)
    private String conversationId;

    @Column(name = "title", nullable = false)
    private String title;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
