package com.prj2.booksta.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AuthorTest {

    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author();
    }

    @Nested
    @DisplayName("Constructor and Getter tests")
    class ConstructorAndGetterTests {

        @Test
        @DisplayName("Should create author with no-args constructor")
        void testNoArgsConstructor() {
            Author newAuthor = new Author();

            assertNull(newAuthor.getId());
            assertNull(newAuthor.getFirstName());
            assertNull(newAuthor.getLastName());
            assertNotNull(newAuthor.getBooks());
            assertTrue(newAuthor.getBooks().isEmpty());
            assertNotNull(newAuthor.getFollowers());
            assertTrue(newAuthor.getFollowers().isEmpty());
            assertNull(newAuthor.getImage());
            assertNull(newAuthor.getUser());
        }

        @Test
        @DisplayName("Should create author with all-args constructor")
        void testAllArgsConstructor() {
            Set<Book> books = new HashSet<>();
            Set<User> followers = new HashSet<>();
            Image image = new Image("http://example.com/author.jpg");
            User user = new User();
            user.setId(1L);

            Author newAuthor = new Author(
                    1L,
                    "John",
                    "Doe",
                    books,
                    followers,
                    image,
                    user
            );

            assertEquals(1L, newAuthor.getId());
            assertEquals("John", newAuthor.getFirstName());
            assertEquals("Doe", newAuthor.getLastName());
            assertSame(books, newAuthor.getBooks());
            assertSame(followers, newAuthor.getFollowers());
            assertSame(image, newAuthor.getImage());
            assertSame(user, newAuthor.getUser());
        }
    }

    @Nested
    @DisplayName("Setter tests")
    class SetterTests {

        @Test
        @DisplayName("Should set and get id")
        void testSetId() {
            author.setId(1L);
            assertEquals(1L, author.getId());
        }

        @Test
        @DisplayName("Should set and get firstName")
        void testSetFirstName() {
            author.setFirstName("John");
            assertEquals("John", author.getFirstName());
        }

        @Test
        @DisplayName("Should set and get lastName")
        void testSetLastName() {
            author.setLastName("Doe");
            assertEquals("Doe", author.getLastName());
        }

        @Test
        @DisplayName("Should set and get books")
        void testSetBooks() {
            Set<Book> books = new HashSet<>();
            Book book = new Book();
            book.setIsbn("9781234567890");
            books.add(book);

            author.setBooks(books);

            assertEquals(1, author.getBooks().size());
            assertTrue(author.getBooks().contains(book));
        }

        @Test
        @DisplayName("Should set and get followers")
        void testSetFollowers() {
            Set<User> followers = new HashSet<>();
            User follower = new User();
            follower.setId(1L);
            followers.add(follower);

            author.setFollowers(followers);

            assertEquals(1, author.getFollowers().size());
            assertTrue(author.getFollowers().contains(follower));
        }

        @Test
        @DisplayName("Should set and get image")
        void testSetImage() {
            Image image = new Image("http://example.com/author.jpg");
            author.setImage(image);
            assertEquals(image, author.getImage());
        }

        @Test
        @DisplayName("Should set and get user")
        void testSetUser() {
            User user = new User();
            user.setId(1L);
            user.setEmail("john@test.com");

            author.setUser(user);

            assertEquals(user, author.getUser());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when same id, firstName, lastName, image, user")
        void testEquals_SameFields_ReturnsTrue() {
            Author author1 = new Author();
            author1.setId(1L);
            author1.setFirstName("John");
            author1.setLastName("Doe");
            author1.setBooks(new HashSet<>());
            author1.setFollowers(new HashSet<>());

            Author author2 = new Author();
            author2.setId(1L);
            author2.setFirstName("John");
            author2.setLastName("Doe");
            author2.setBooks(null); // books excluded from equals
            author2.setFollowers(null); // followers excluded from equals

            assertEquals(author1, author2);
        }

        @Test
        @DisplayName("Should have same hashCode when equal")
        void testHashCode_EqualObjects_SameHashCode() {
            Author author1 = new Author();
            author1.setId(1L);
            author1.setFirstName("John");
            author1.setLastName("Doe");

            Author author2 = new Author();
            author2.setId(1L);
            author2.setFirstName("John");
            author2.setLastName("Doe");

            assertEquals(author1.hashCode(), author2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different id")
        void testEquals_DifferentId_ReturnsFalse() {
            Author author1 = new Author();
            author1.setId(1L);
            author1.setFirstName("John");
            author1.setLastName("Doe");

            Author author2 = new Author();
            author2.setId(2L);
            author2.setFirstName("John");
            author2.setLastName("Doe");

            assertNotEquals(author1, author2);
        }

        @Test
        @DisplayName("Should not be equal when different firstName")
        void testEquals_DifferentFirstName_ReturnsFalse() {
            Author author1 = new Author();
            author1.setId(1L);
            author1.setFirstName("John");
            author1.setLastName("Doe");

            Author author2 = new Author();
            author2.setId(1L);
            author2.setFirstName("Jane");
            author2.setLastName("Doe");

            assertNotEquals(author1, author2);
        }

        @Test
        @DisplayName("Books and followers should be excluded from equals")
        void testEquals_DifferentBooksAndFollowers_StillEqual() {
            Set<Book> books1 = new HashSet<>();
            Book book = new Book();
            book.setIsbn("9781234567890");
            books1.add(book);

            Set<User> followers1 = new HashSet<>();
            User follower = new User();
            follower.setId(1L);
            followers1.add(follower);

            Author author1 = new Author();
            author1.setId(1L);
            author1.setFirstName("John");
            author1.setLastName("Doe");
            author1.setBooks(books1);
            author1.setFollowers(followers1);

            Author author2 = new Author();
            author2.setId(1L);
            author2.setFirstName("John");
            author2.setLastName("Doe");
            author2.setBooks(new HashSet<>()); // different books
            author2.setFollowers(new HashSet<>()); // different followers

            assertEquals(author1, author2);
        }
    }

    @Nested
    @DisplayName("ToString tests")
    class ToStringTests {

        @Test
        @DisplayName("Should not include books and followers in toString")
        void testToString_ExcludesBooksAndFollowers() {
            author.setId(1L);
            author.setFirstName("John");
            author.setLastName("Doe");

            Set<Book> books = new HashSet<>();
            Book book = new Book();
            book.setIsbn("9781234567890");
            books.add(book);
            author.setBooks(books);

            String toString = author.toString();

            assertTrue(toString.contains("John"));
            assertTrue(toString.contains("Doe"));
            assertFalse(toString.contains("books"));
            assertFalse(toString.contains("followers"));
        }
    }

    @Nested
    @DisplayName("Relationship management tests")
    class RelationshipTests {

        @Test
        @DisplayName("Should add book to author's books")
        void testAddBook() {
            author.setBooks(new HashSet<>());
            Book book = new Book();
            book.setIsbn("9781234567890");

            author.getBooks().add(book);

            assertEquals(1, author.getBooks().size());
            assertTrue(author.getBooks().contains(book));
        }

        @Test
        @DisplayName("Should remove book from author's books")
        void testRemoveBook() {
            author.setBooks(new HashSet<>());
            Book book = new Book();
            book.setIsbn("9781234567890");
            author.getBooks().add(book);

            author.getBooks().remove(book);

            assertTrue(author.getBooks().isEmpty());
        }

        @Test
        @DisplayName("Should add follower to author")
        void testAddFollower() {
            author.setFollowers(new HashSet<>());
            User follower = new User();
            follower.setId(1L);

            author.getFollowers().add(follower);

            assertEquals(1, author.getFollowers().size());
            assertTrue(author.getFollowers().contains(follower));
        }

        @Test
        @DisplayName("Should remove follower from author")
        void testRemoveFollower() {
            author.setFollowers(new HashSet<>());
            User follower = new User();
            follower.setId(1L);
            author.getFollowers().add(follower);

            author.getFollowers().remove(follower);

            assertTrue(author.getFollowers().isEmpty());
        }
    }
}
