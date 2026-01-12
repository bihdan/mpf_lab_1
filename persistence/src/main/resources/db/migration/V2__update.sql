CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255),
                       role VARCHAR(50)
);

ALTER TABLE comments ADD COLUMN user_id BIGINT;

ALTER TABLE comments ADD CONSTRAINT fk_comment_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;