DROP TABLE user_tests;

-- User tests (kết quả làm bài của user)
CREATE TABLE user_tests
(
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                   BIGINT NOT NULL,
    test_id                   BIGINT NOT NULL,
    mode                      ENUM('PRACTICE', 'EXAM') NOT NULL,
    -- General fields
    total_questions           INT,
    correct_answers           INT,
    correct_percent           DECIMAL(5, 2),
    time_spent                INT,  -- in seconds
    -- Specific fields for practice mode
    parts                     JSON, -- Part 1, Part 2, ...
    -- Specific fields for exam mode
    total_score               INT,
    listening_score           INT,
    reading_score             INT,
    listening_correct_answers INT,
    reading_correct_answers   INT,
    created_at                DATETIME,
    updated_at                DATETIME,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (test_id) REFERENCES tests (id) ON DELETE CASCADE
);