CREATE TABLE comments
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(2000) NOT NULL,
    user_id BIGINT NOT NULL,
    test_id BIGINT NOT NULL,
    question_id BIGINT,
    parent_id BIGINT,
    created_at DATETIME,
    updated_at DATETIME,

    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_comment_test FOREIGN KEY (test_id) REFERENCES tests(id),
    CONSTRAINT fk_comment_question FOREIGN KEY (question_id) REFERENCES questions(id),
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE
)