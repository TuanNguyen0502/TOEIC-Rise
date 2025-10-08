CREATE TABLE chatbot_ratings
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id            BIGINT       NOT NULL,
    message_id         VARCHAR(255) NOT NULL,
    conversation_title VARCHAR(255) NOT NULL,
    message            TEXT         NOT NULL,
    rating             ENUM('LIKE', 'DISLIKE') NOT NULL,
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL,
    FOREIGN KEY (message_id) REFERENCES chat_memories (id) ON DELETE SET NULL
);