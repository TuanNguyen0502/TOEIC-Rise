ALTER TABLE test_sets
    ADD COLUMN status ENUM('IN_USE', 'DELETED') AFTER name;