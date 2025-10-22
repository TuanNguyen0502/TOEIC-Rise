package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.models.enums.EChatbotRating;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chatbot_ratings")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotRating extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "conversation_title", nullable = false)
    private String conversationTitle;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "rating", nullable = false)
    @Enumerated(EnumType.STRING)
    private EChatbotRating rating;
}
