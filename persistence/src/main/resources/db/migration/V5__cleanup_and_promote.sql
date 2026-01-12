ALTER TABLE users DROP COLUMN username;

UPDATE users SET role = 'ADMIN', verified = TRUE WHERE email = 'admin';
UPDATE users SET verified = TRUE WHERE email = 'student';