CREATE TABLE flashcard_item_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    flashcard_item_id BIGINT NOT NULL,
    level VARCHAR(50),
    next_review_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_flashcard_item UNIQUE (user_id, flashcard_item_id),
    CONSTRAINT fk_fip_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_fip_flashcard_item FOREIGN KEY (flashcard_item_id) REFERENCES flashcard_items(id)
);