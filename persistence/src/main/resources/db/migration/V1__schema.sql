-- Таблиця книг
CREATE TABLE books (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255),
                       pub_year INT
);

-- Базова таблиця коментарів
CREATE TABLE comments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          text TEXT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          book_id BIGINT,
                          CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);