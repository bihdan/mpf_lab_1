-- Створення таблиці користувачів
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255),
                       role VARCHAR(50)
);

-- Оновлення таблиці коментарів: додаємо зв'язок із користувачем
ALTER TABLE comments ADD COLUMN user_id BIGINT;

-- Додавання зовнішнього ключа (SET NULL дозволяє зберегти відгук, якщо юзера видалено)
ALTER TABLE comments ADD CONSTRAINT fk_comment_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;