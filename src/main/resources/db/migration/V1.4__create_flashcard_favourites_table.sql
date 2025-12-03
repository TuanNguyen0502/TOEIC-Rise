CREATE TABLE flashcard_favourites
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    flashcard_id BIGINT NOT NULL,
    created_at   DATETIME,
    updated_at   DATETIME,
    CONSTRAINT fk_ff_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_ff_flashcard FOREIGN KEY (flashcard_id) REFERENCES flashcards (id),
    CONSTRAINT uc_user_flashcard UNIQUE (user_id, flashcard_id)
);