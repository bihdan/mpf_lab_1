INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN');
INSERT INTO users (username, password, role) VALUES ('student_sumdu', 'pass123', 'USER');

INSERT INTO books (title, author, pub_year) VALUES ('Clean Code', 'Robert Martin', 2008);
INSERT INTO books (title, author, pub_year) VALUES ('Spring Boot in Action', 'Craig Walls', 2016);

INSERT INTO comments (text, book_id, user_id, created_at)
VALUES ('Дуже корисна книга для початківців!', 1, 2, CURRENT_TIMESTAMP);
INSERT INTO comments (text, book_id, user_id, created_at)
VALUES ('JPA частина просто супер.', 2, 2, CURRENT_TIMESTAMP);