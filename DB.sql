CREATE DATABASE webp;

USE webp;

CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(120) NOT NULL UNIQUE,
  passwd VARCHAR(255) NOT NULL,
  user_role enum('admin','user') NOT NULL DEFAULT 'user'
);

CREATE TABLE books(
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  author VARCHAR(255) NOT NULL,
  image VARCHAR(255) NOT NULL,
  cyear INT 
);
  
CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    book_id INT NOT NULL,
    FOREIGN KEY (book_id) REFERENCES books(id)
);


INSERT INTO users (username, passwd, user_role) VALUES ("admin","$2a$10$5hIRKaCgjHMVA5f4pjKnm.P8f/Buyg1FXLwMd5Eek3H9UhkcoCtMO","admin");
