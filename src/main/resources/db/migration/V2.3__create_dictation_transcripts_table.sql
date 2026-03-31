CREATE TABLE dictation_transcripts
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_group_id BIGINT NOT NULL,

    question_text     TEXT,
    options           JSON,
    passage_text      TEXT,

    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_dictation_transcript_group
        FOREIGN KEY (question_group_id)
            REFERENCES question_groups (id)
            ON DELETE CASCADE,

    CONSTRAINT uk_dictation_transcript_group
        UNIQUE (question_group_id)
);