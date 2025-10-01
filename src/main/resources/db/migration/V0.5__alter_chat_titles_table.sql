DROP TABLE chat_titles;

CREATE TABLE chat_titles
(
    id              CHAR(36) PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    conversation_id VARCHAR(255) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE (user_id, conversation_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
