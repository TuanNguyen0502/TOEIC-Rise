CREATE TABLE IF NOT EXISTS user_learning_paths (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    learning_path_id BIGINT NOT NULL,
    selected_at DATETIME NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT uc_user_learning_path UNIQUE (user_id, learning_path_id),
    CONSTRAINT fk_ulp_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ulp_learning_path FOREIGN KEY (learning_path_id) REFERENCES learning_paths(id)
);
