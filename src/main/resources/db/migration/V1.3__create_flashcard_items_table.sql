CREATE TABLE flashcard_items
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    flashcard_id  BIGINT       NOT NULL,
    vocabulary    VARCHAR(255) NOT NULL,
    definition    TEXT         NOT NULL,
    audio_url     VARCHAR(512),
    pronunciation VARCHAR(255),
    created_at    DATETIME,
    updated_at    DATETIME,
    CONSTRAINT fk_flashcard_item_flashcard FOREIGN KEY (flashcard_id) REFERENCES flashcards (id)
);