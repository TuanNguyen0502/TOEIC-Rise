ALTER TABLE user_tests
    ADD COLUMN total_listening_questions INTEGER AFTER reading_correct_answers,
    ADD COLUMN total_reading_questions INTEGER AFTER total_listening_questions;