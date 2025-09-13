-- Roles
CREATE TABLE roles
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        ENUM('ADMIN','LEARNER', 'STAFF') NOT NULL UNIQUE,
    description TEXT,
    created_at  DATETIME,
    updated_at  DATETIME
);

INSERT INTO roles (name, description, created_at, updated_at)
VALUES ('ADMIN', 'Administrator with full access', NOW(), NOW()),
       ('LEARNER', 'Learner with access to learning materials', NOW(), NOW()),
       ('STAFF', 'Staff member with limited access', NOW(), NOW());

-- Accounts
CREATE TABLE accounts
(
    id                               BIGINT AUTO_INCREMENT PRIMARY KEY,
    email                            VARCHAR(255) NOT NULL UNIQUE,
    password                         VARCHAR(255) NOT NULL,
    auth_provider                    VARCHAR(50),
    is_active                        BOOLEAN DEFAULT TRUE,
    verification_code                VARCHAR(255),
    verfication_code_expires_at      DATETIME,
    failed_login_attempts            INT     DEFAULT 0,
    account_locked_until             DATETIME,
    resend_verification_attempts     INT     DEFAULT 0,
    resend_verification_locked_until DATETIME,
    created_at                       DATETIME,
    updated_at                       DATETIME
);

-- Tạo tài khoản admin mặc định
-- Mật khẩu: Admin@toeicrise2025
INSERT INTO `accounts`
VALUES (1, 'admin@toeic-rise.com', '$2a$10$k82KIubG8RXFQ2ad7rQCJ.efujvRWBM7CzgXNwEDZohWyOnbrRuc6', NULL, 1, NULL, NULL,
        0, NULL, 0, NULL, '2025-09-12 23:26:06', '2025-09-12 23:28:06');

-- Refresh Tokens
CREATE TABLE refresh_tokens
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    token       VARCHAR(255) NOT NULL,
    account_id  BIGINT       NOT NULL,
    expiry_date TIMESTAMP    NOT NULL,
    FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE
);

-- Users
CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT       NOT NULL,
    role_id    BIGINT       NOT NULL,
    full_name  VARCHAR(255) NOT NULL,
    gender     ENUM('MALE','FEMALE','OTHER') DEFAULT 'OTHER',
    avatar     VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE RESTRICT
);

-- Tạo user admin mặc định
-- Liên kết với account admin đã tạo ở trên
-- Liên kết với role ADMIN đã tạo ở trên
INSERT INTO `users`
VALUES (1, 1, 1, 'Administrator', NULL, NULL, '2025-09-12 23:26:06', '2025-09-12 23:26:06');

-- Chat conversations
CREATE TABLE chat_conversations
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    title   VARCHAR(255),
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Chat memories
CREATE TABLE chat_memories
(
    id                   VARCHAR(255) PRIMARY KEY,
    conversation_id      VARCHAR(255) NOT NULL,
    message_type         VARCHAR(50)  NOT NULL,
    content              TEXT         NOT NULL,
    metadata             TEXT,
    created_at           DATETIME,
    chat_conversation_id BIGINT       NOT NULL,
    FOREIGN KEY (chat_conversation_id) REFERENCES chat_conversations (id) ON DELETE CASCADE
);

-- Chat ratings
CREATE TABLE chat_ratings
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    message_id VARCHAR(255) NOT NULL,
    rating     ENUM('LIKE', 'DISLIKE') NOT NULL,
    created_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (message_id) REFERENCES chat_memories (id) ON DELETE CASCADE
);

-- Parts
CREATE TABLE parts
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL UNIQUE,
    created_at DATETIME,
    updated_at DATETIME
);

-- Tags
CREATE TABLE tags
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME,
    updated_at DATETIME
);

-- Test sets (bộ đề)
CREATE TABLE test_sets
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME,
    updated_at DATETIME
);

-- Tests (đề thi cụ thể)
CREATE TABLE tests
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_set_id BIGINT,
    name        VARCHAR(255) NOT NULL,
    created_at  DATETIME,
    updated_at  DATETIME,
    FOREIGN KEY (test_set_id) REFERENCES test_sets (id) ON DELETE SET NULL
);

-- Question groups (một đoạn audio/image + passage có thể chứa nhiều câu)
CREATE TABLE question_groups
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    audio_url  VARCHAR(255),
    image_url  VARCHAR(255),
    position   INT    NOT NULL,
    passage    TEXT,
    test_id    BIGINT NOT NULL,
    part_id    BIGINT NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (test_id) REFERENCES tests (id) ON DELETE CASCADE,
    FOREIGN KEY (part_id) REFERENCES parts (id) ON DELETE CASCADE
);

-- Questions
CREATE TABLE questions
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_group_id BIGINT  NOT NULL,
    position          INT     NOT NULL,
    text              TEXT,
    options           JSON    NOT NULL, -- lưu A,B,C,D dạng JSON
    correct_option    CHAR(1) NOT NULL, -- A/B/C/D
    explanations      TEXT,
    created_at        DATETIME,
    updated_at        DATETIME,
    FOREIGN KEY (question_group_id) REFERENCES question_groups (id) ON DELETE CASCADE
);

-- Bảng trung gian (Many-to-Many)
CREATE TABLE question_tags
(
    question_id BIGINT NOT NULL,
    tag_id      BIGINT NOT NULL,
    PRIMARY KEY (question_id, tag_id),
    FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
);

-- Question transcripts (lưu transcript audio)
CREATE TABLE question_transcripts
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_group_id BIGINT NOT NULL,
    transcript        TEXT   NOT NULL,
    created_at        DATETIME,
    updated_at        DATETIME,
    FOREIGN KEY (question_group_id) REFERENCES question_groups (id) ON DELETE CASCADE
);

-- User tests (kết quả làm bài của user)
CREATE TABLE user_tests
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    test_id    BIGINT NOT NULL,
    parts      JSON, -- điểm theo từng part (JSON)
    score      INT,
    time_spent INT,  -- tổng thời gian (giây)
    created_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (test_id) REFERENCES tests (id) ON DELETE CASCADE
);

-- User answers (đáp án người dùng chọn)
CREATE TABLE user_answers
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_test_id BIGINT  NOT NULL,
    question_id  BIGINT  NOT NULL,
    answer       CHAR(1) NOT NULL,
    is_correct   BOOLEAN,
    created_at   DATETIME,
    FOREIGN KEY (user_test_id) REFERENCES user_tests (id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE
);
