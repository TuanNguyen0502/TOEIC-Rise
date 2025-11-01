DROP TABLE user_answers;

-- User answers table
CREATE TABLE user_answers
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_test_id      BIGINT  NOT NULL,
    question_id       BIGINT  NOT NULL,
    question_group_id BIGINT  NOT NULL,
    answer            CHAR(1) NOT NULL,
    is_correct        BOOLEAN,
    created_at        DATETIME,
    updated_at        DATETIME,
    FOREIGN KEY (user_test_id) REFERENCES user_tests (id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE
);