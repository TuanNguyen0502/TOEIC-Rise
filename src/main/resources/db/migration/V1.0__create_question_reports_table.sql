CREATE TABLE question_reports
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id   BIGINT NOT NULL,
    reporter_id   BIGINT NOT NULL,
    resolver_id   BIGINT NULL,
    reasons       JSON   NOT NULL, -- List of reasons (e.g., ["WRONG_ANSWER,", "TYPO", "WRONG_EXPLANATION"])
    description   TEXT,
    status        ENUM('PENDING', 'REVIEWING', 'RESOLVED', 'REJECTED') DEFAULT 'PENDING',
    resolved_note TEXT,
    created_at    DATETIME,
    updated_at    DATETIME,
    CONSTRAINT fk_qr_question FOREIGN KEY (question_id) REFERENCES questions (id),
    CONSTRAINT fk_qr_reporter FOREIGN KEY (reporter_id) REFERENCES users (id),
    CONSTRAINT fk_qr_resolver FOREIGN KEY (resolver_id) REFERENCES users (id)
);