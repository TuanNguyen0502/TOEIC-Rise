-- Update foreign key constraints to enable CASCADE DELETE
ALTER TABLE flashcard_items
DROP FOREIGN KEY fk_flashcard_item_flashcard;

ALTER TABLE flashcard_items
    ADD CONSTRAINT fk_flashcard_item_flashcard
        FOREIGN KEY (flashcard_id) REFERENCES flashcards (id) ON DELETE CASCADE;

ALTER TABLE flashcard_favourites
DROP FOREIGN KEY fk_ff_flashcard;

ALTER TABLE flashcard_favourites
    ADD CONSTRAINT fk_ff_flashcard
        FOREIGN KEY (flashcard_id) REFERENCES flashcards (id) ON DELETE CASCADE;
