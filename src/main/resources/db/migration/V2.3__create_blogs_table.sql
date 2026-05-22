CREATE TABLE blog_categories
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL UNIQUE,
    slug       VARCHAR(255) NOT NULL UNIQUE,
    is_active  BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE blog_posts
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    slug          VARCHAR(255) NOT NULL UNIQUE,
    summary       TEXT,
    content       LONGTEXT     NOT NULL,
    thumbnail_url VARCHAR(255),
    author_id     BIGINT       NOT NULL,
    category_id   BIGINT,
    status        ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') DEFAULT 'DRAFT',
    views         INT DEFAULT 0,
    created_at    DATETIME,
    updated_at    DATETIME,
    FOREIGN KEY (author_id) REFERENCES users (id),
    FOREIGN KEY (category_id) REFERENCES blog_categories (id) ON DELETE SET NULL
);
