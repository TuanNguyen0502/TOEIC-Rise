CREATE TABLE flashcards
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    access_type ENUM('PRIVATE', 'PUBLIC') DEFAULT 'PRIVATE' NOT NULL,
    created_at  DATETIME,
    updated_at  DATETIME,
    CONSTRAINT fk_flashcard_user FOREIGN KEY (user_id) REFERENCES users (id)
);