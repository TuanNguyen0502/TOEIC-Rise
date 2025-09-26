package com.hcmute.fit.toeicrise.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_titles")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatTitle extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "conversation_id", nullable = false)
    private String conversationId;

    @Column(name = "title", nullable = false)
    private String title;
}
