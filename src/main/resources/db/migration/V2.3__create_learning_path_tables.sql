CREATE TABLE IF NOT EXISTS learning_paths (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS lessons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    learning_path_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    video_url VARCHAR(500),
    topic VARCHAR(255),
    level VARCHAR(50) NOT NULL,
    content TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    order_index INT NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT fk_lessons_learning_path FOREIGN KEY (learning_path_id) REFERENCES learning_paths(id),
    CONSTRAINT uc_lessons_path_order UNIQUE (learning_path_id, order_index)
);

CREATE TABLE IF NOT EXISTS user_lesson_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    lesson_id BIGINT NOT NULL,
    progress_percentage DECIMAL(5,2) NOT NULL DEFAULT 0,
    last_watched_time_ms BIGINT NOT NULL DEFAULT 0,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT fk_ulp_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ulp_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id),
    CONSTRAINT uc_ulp_user_lesson UNIQUE (user_id, lesson_id)
);
