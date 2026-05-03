ALTER TABLE learning_paths
    ADD COLUMN slug VARCHAR(255) NULL AFTER name,

UPDATE learning_paths SET slug = CONCAT('learning-path-', id) WHERE slug IS NULL;

ALTER TABLE learning_paths
    MODIFY COLUMN slug VARCHAR(255) NOT NULL,
    ADD CONSTRAINT uk_learning_paths_slug UNIQUE (slug);

ALTER TABLE lessons
    ADD COLUMN slug VARCHAR(255) NULL AFTER title,
    ADD COLUMN practice VARCHAR(255) NULL AFTER slug;

UPDATE lessons SET slug = CONCAT('lesson-', id) WHERE slug IS NULL;

ALTER TABLE lessons
    MODIFY COLUMN slug VARCHAR(255) NOT NULL,
    ADD CONSTRAINT uc_lessons_path_slug UNIQUE (learning_path_id, slug);
