CREATE TABLE game_session_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_session_id BIGINT NOT NULL,
    flashcard_item_id BIGINT NOT NULL,
    is_correct BOOLEAN,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_gsi_session FOREIGN KEY (game_session_id) REFERENCES game_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_gsi_flashcard_item FOREIGN KEY (flashcard_item_id) REFERENCES flashcard_items(id)
);