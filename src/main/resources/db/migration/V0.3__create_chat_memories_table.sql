CREATE TABLE system_prompts
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    content    TEXT NOT NULL,
    version    INT  NOT NULL,
    is_active  BOOLEAN NOT NULL ,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE chat_memories
(
    id              VARCHAR(255) PRIMARY KEY,
    conversation_id VARCHAR(255) NOT NULL,
    message_type    VARCHAR(50)  NOT NULL,
    content         TEXT         NOT NULL,
    metadata        TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE chat_titles
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    conversation_id VARCHAR(255) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE (user_id, conversation_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);