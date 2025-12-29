-- ============================
-- SUBJECTS
-- ============================
INSERT INTO subjects (name) VALUES ('Fantasy');
INSERT INTO subjects (name) VALUES ('Science Fiction');
INSERT INTO subjects (name) VALUES ('Romance');

-- ============================
-- AUTHORS
-- ============================
INSERT INTO authors (first_name, last_name, user_id)
VALUES ('Sarah', 'Brennan', NULL);

INSERT INTO authors (first_name, last_name, user_id)
VALUES ('Mackenzi', 'Lee', null);

INSERT INTO authors (first_name, last_name, user_id)
VALUES ('Mark', 'Oshiro', null);

-- ============================
-- IMAGES
-- ============================
INSERT INTO image (url)
VALUES ('https://ia600100.us.archive.org/view_archive.php?archive=/5/items/l_covers_0012/l_covers_0012_35.zip&file=0012354250-L.jpg');

INSERT INTO image (url)
VALUES ('https://ia601705.us.archive.org/view_archive.php?archive=/29/items/l_covers_0008/l_covers_0008_36.zip&file=0008367802-L.jpg');

INSERT INTO image (url)
VALUES ('https://ia800100.us.archive.org/view_archive.php?archive=/5/items/l_covers_0012/l_covers_0012_36.zip&file=0012366752-L.jpg');

-- ============================
-- BOOK
-- ============================
INSERT INTO book (
    isbn, title, publishing_year, description, pages, image_id
)
VALUES (
           '9781250167026',
           'Anger is a Gift',
           2018,
           '**MOSS JEFFRIES** is many things--considerate student, devoted son, loyal friend and affectionate boyfriend, enthusiastic nerd.

           But sometimes Moss still wishes he could be someone else--someone without panic attacks, someone whose father was still alive, someone who hadn''t become a rallying point for a community because of one horrible night.

           And most of all, he wishes he didn''t feel so stuck.

           Moss can''t even escape at school--he and his friends are subject to the lack of funds and crumbling infrastructure at West Oakland High, as well as constant intimidation by the resource officer stationed in their halls. It feels sometimes that the students are treated more like criminals.

           Something needs to change--but who will listen to a group of teens?

           When tensions hit a fever pitch and tragedy strikes again, Moss must face a difficult choice: give in to fear and hate or realize that anger can actually be a gift.

           This description comes from the publisher.',
           463,
           3
       );

INSERT INTO book (
    isbn, title, publishing_year, description, pages, image_id
)
VALUES (
           '9789877473216',
           'The Gentleman''s Guide to Vice and Virtue',
           2017,
           'Henry "Monty" Montague was born and bred to be a gentleman, but he was never one to be tamed. The finest boarding schools in England and the constant disapproval of his father haven''t been able to curb any of his roguish passions--not for gambling halls, late nights spent with a bottle of spirits, or waking up in the arms of women or men.

           But as Monty embarks on his Grand Tour of Europe, his quest for a life filled with pleasure and vice is in danger of coming to an end. Not only does his father expect him to take over the family''s estate upon his return, but Monty is also nursing an impossible crush on his best friend and traveling companion, Percy.

           Still, it isn''t in Monty''s nature to give up. Even with his younger sister, Felicity, in tow, he vows to make this yearlong escapade one last hedonistic hurrah and flirt with Percy from Paris to Rome. But when one of Monty''s reckless decisions turns their trip abroad into a harrowing manhunt that spans across Europe, it calls into question everything he knows, including his relationship with the boy he adores.',
           445,
           2
       );

INSERT INTO book (
    isbn, title, publishing_year, description, pages, image_id
)
VALUES (
           '9781618731203',
           'In Other Lands',
           2017,
           'The Borderlands aren’’t like anywhere else. Don’’t try to smuggle a phone or any other piece of technology over the wall that marks the Border — unless you enjoy a fireworks display in your backpack. (Ballpoint pens are okay.) There are elves, harpies, and — best of all as far as Elliot is concerned — mermaids.

           "What’’s your name?"

           "Serene."

           "Serena?" Elliot asked.

           "Serene," said Serene. "My full name is Serene-Heart-in-the-Chaos-of-Battle."

           Elliot’’s mouth fell open. "That is badass."

           Elliot? Who’’s Elliot? Elliot is thirteen years old. He’’s smart and just a tiny bit obnoxious. Sometimes more than a tiny bit. When his class goes on a field trip and he can see a wall that no one else can see, he is given the chance to go to school in the Borderlands.

           It turns out that on the other side of the wall, classes involve a lot more weaponry and fitness training and fewer mermaids than he expected. On the other hand, there’’s Serene-Heart-in-the-Chaos-of-Battle, an elven warrior who is more beautiful than anyone Elliot has ever seen, and then there’’s her human friend Luke: sunny, blond, and annoyingly likeable. There are lots of interesting books. There’’s even the chance Elliot might be able to change the world.',
           437,
           1
       );

-- ============================
-- BOOK ↔ AUTHORS LINK
-- ============================
INSERT INTO book_authors (book_isbn, authors_id)
VALUES ('9781618731203', 1);

INSERT INTO book_authors (book_isbn, authors_id)
VALUES ('9789877473216', 2);

INSERT INTO book_authors (book_isbn, authors_id)
VALUES ('9781250167026', 3);

-- ============================
-- BOOK ↔ SUBJECTS LINK
-- ============================
INSERT INTO book_subjects (book_isbn, subjects_id)
VALUES ('9781618731203', 1);

INSERT INTO book_subjects (book_isbn, subjects_id)
VALUES ('9789877473216', 1);

INSERT INTO book_subjects (book_isbn, subjects_id)
VALUES ('9781250167026', 1);
-- =====================================================
-- ROLES
-- =====================================================
INSERT INTO role (id, name) VALUES (1, 'USER');
INSERT INTO role (id, name) VALUES (2, 'AUTHOR');
INSERT INTO role (id, name) VALUES (3, 'LIBRARIAN');
INSERT INTO role (id, name) VALUES (4, 'SELLER');
INSERT INTO role (id, name) VALUES (5, 'ADMIN');

-- =====================================================
-- PRIVILEGES
-- =====================================================

-- Content Discovery
INSERT INTO privilege (id, name) VALUES (1, 'BOOK_VIEW');
INSERT INTO privilege (id, name) VALUES (2, 'AUTHOR_VIEW');

-- Personal Library
INSERT INTO privilege (id, name) VALUES (10, 'LIBRARY_MANAGE');
INSERT INTO privilege (id, name) VALUES (11, 'READING_TRACK');

-- Social Features
INSERT INTO privilege (id, name) VALUES (20, 'REVIEW_WRITE');
INSERT INTO privilege (id, name) VALUES (21, 'SOCIAL_INTERACT');
INSERT INTO privilege (id, name) VALUES (22, 'GROUP_MANAGE');

-- Commerce
INSERT INTO privilege (id, name) VALUES (30, 'SELLER_VIEW');

-- Author privileges
INSERT INTO privilege (id, name) VALUES (100, 'BOOK_PUBLISH');
INSERT INTO privilege (id, name) VALUES (101, 'AUTHOR_CONTENT_MANAGE');
INSERT INTO privilege (id, name) VALUES (102, 'AUTHOR_ANALYTICS');

-- Librarian
INSERT INTO privilege (id, name) VALUES (200, 'BOOK_EDIT');
INSERT INTO privilege (id, name) VALUES (201, 'CONTENT_MODERATE');

-- Seller
INSERT INTO privilege (id, name) VALUES (300, 'INVENTORY_MANAGE');

-- =====================================================
-- ROLE → PRIVILEGE ASSIGNMENTS
-- =====================================================

-- ROLE_USER
INSERT INTO roles_privileges (role_id, privilege_id) VALUES
                                                         (1, 1), (1, 2),
                                                         (1, 10), (1, 11),
                                                         (1, 20), (1, 21), (1, 22),
                                                         (1, 30);

-- ROLE_AUTHOR
INSERT INTO roles_privileges (role_id, privilege_id) VALUES
                                                         (2, 1), (2, 2), (2, 10), (2, 11),
                                                         (2, 20), (2, 21), (2, 22), (2, 30),
                                                         (2, 100), (2, 101), (2, 102);

-- ROLE_LIBRARIAN
INSERT INTO roles_privileges (role_id, privilege_id) VALUES
                                                         (3, 1), (3, 2), (3, 10), (3, 11),
                                                         (3, 20), (3, 21), (3, 22), (3, 30),
                                                         (3, 200), (3, 201);

-- ROLE_SELLER
INSERT INTO roles_privileges (role_id, privilege_id) VALUES
                                                         (4, 1), (4, 2), (4, 10), (4, 11),
                                                         (4, 30), (4, 300);

-- ROLE_ADMIN → all privileges
INSERT INTO roles_privileges (role_id, privilege_id)
SELECT 5, id FROM privilege;



-- ============================
-- USER DE DEMO
-- ============================
INSERT INTO users (
    id,
    first_name,
    last_name,
    email,
    password,
    google_id,
    picture
)
VALUES (
    100,
    'Demo',
    'User',
    'demo@booksta.local',
    'password',
    NULL,
    NULL
);

-- Donne-lui le rôle USER
INSERT INTO users_roles (user_id, role_id)
VALUES (100, 1);

-- IMPORTANT : éviter que la séquence recrée un user avec id = 100
-- (optionnel, mais recommandé si tu utilises vraiment la sequence user_sequence)
ALTER SEQUENCE user_sequence RESTART WITH 101;

-- ============================
-- LIVRES POSSEDES PAR LE USER 100
-- (table de jointure user_owned_books)
-- ============================
INSERT INTO user_owned_books (user_id, book_isbn)
VALUES (100, '9781250167026');      -- Anger is a Gift

INSERT INTO user_owned_books (user_id, book_isbn)
VALUES (100, '9789877473216');      -- The Gentleman's Guide...

INSERT INTO user_owned_books (user_id, book_isbn)
VALUES (100, '9781618731203');      -- In Other Lands

