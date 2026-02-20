CREATE SEQUENCE IF NOT EXISTS book_sequence START 1 INCREMENT 1;

-- Insertion de sujets
INSERT INTO subjects (id, name) VALUES (1, 'Fantasy');
INSERT INTO subjects (id, name) VALUES (2, 'Science Fiction');
INSERT INTO subjects (id, name) VALUES (3, 'Romance');

-- Insertion d'auteurs
INSERT INTO authors (id, first_name, last_name) VALUES (1, 'J.K.', 'Rowling');
INSERT INTO authors (id, first_name, last_name) VALUES (2, 'J.R.R.', 'Tolkien');

-- Insertion de livres (utilisation de la séquence)
INSERT INTO book (isbn, title, publishing_year, description)
VALUES (nextval('book_sequence'), 'Harry Potter à l''école des sorciers', 1997, 'Premier tome');
INSERT INTO book (isbn, title, publishing_year, description)
VALUES (nextval('book_sequence'), 'Le Seigneur des Anneaux', 1954, 'Trilogie fantastique');

-- À ce stade :
-- - le premier livre a isbn = 1
-- - le deuxième livre a isbn = 2

-- Liaison livres-auteurs
INSERT INTO book_authors (book_isbn, authors_id) VALUES (1, 1);  -- Harry Potter -> Rowling
INSERT INTO book_authors (book_isbn, authors_id) VALUES (2, 2);  -- Seigneur des Anneaux -> Tolkien

-- Liaison livres-sujets
INSERT INTO book_subjects (book_isbn, subjects_id) VALUES (1, 1); -- Harry Potter -> Fantasy
INSERT INTO book_subjects (book_isbn, subjects_id) VALUES (2, 1); -- SDA -> Fantasy

-- Insertion d'un utilisateur
INSERT INTO users (id, first_name, last_name, email, password)
VALUES (1, 'John', 'Doe', 'john@example.com', 'password123');
