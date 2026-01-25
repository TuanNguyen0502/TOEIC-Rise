ALTER TABLE tags
    ADD COLUMN number_of_user_answers INT DEFAULT 0 AFTER name;
ALTER TABLE tags
    ADD COLUMN user_answers_correction_rate DECIMAL(5, 2) DEFAULT 0.00 AFTER number_of_user_answers;

UPDATE tags
SET number_of_user_answers = 0
WHERE number_of_user_answers IS NULL;

UPDATE tags
SET user_answers_correction_rate = 0.00
WHERE user_answers_correction_rate IS NULL;