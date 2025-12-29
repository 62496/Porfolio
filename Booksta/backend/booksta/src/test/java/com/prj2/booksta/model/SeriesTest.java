package com.prj2.booksta.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SeriesTest {

    private Series series;

    @BeforeEach
    void setUp() {
        series = new Series();
    }

    @Nested
    @DisplayName("Constructor and Getter tests")
    class ConstructorAndGetterTests {

        @Test
        @DisplayName("Should create series with no-args constructor")
        void testNoArgsConstructor() {
            Series newSeries = new Series();

            assertNull(newSeries.getId());
            assertNull(newSeries.getTitle());
            assertNull(newSeries.getDescription());
            assertNull(newSeries.getAuthor());
            assertNotNull(newSeries.getBooks());
            assertTrue(newSeries.getBooks().isEmpty());
            assertNotNull(newSeries.getFollowers());
            assertTrue(newSeries.getFollowers().isEmpty());
        }

        @Test
        @DisplayName("Should create series with all-args constructor")
        void testAllArgsConstructor() {
            Author author = new Author();
            author.setId(1L);
            author.setFirstName("John");
            author.setLastName("Doe");

            Set<Book> books = new LinkedHashSet<>();
            Set<User> followers = new HashSet<>();

            Series newSeries = new Series(
                    1L,
                    "Test Series",
                    "A test series description",
                    author,
                    books,
                    followers
            );

            assertEquals(1L, newSeries.getId());
            assertEquals("Test Series", newSeries.getTitle());
            assertEquals("A test series description", newSeries.getDescription());
            assertSame(author, newSeries.getAuthor());
            assertSame(books, newSeries.getBooks());
            assertSame(followers, newSeries.getFollowers());
        }
    }

    @Nested
    @DisplayName("Setter tests")
    class SetterTests {

        @Test
        @DisplayName("Should set and get id")
        void testSetId() {
            series.setId(1L);
            assertEquals(1L, series.getId());
        }

        @Test
        @DisplayName("Should set and get title")
        void testSetTitle() {
            series.setTitle("Epic Fantasy Series");
            assertEquals("Epic Fantasy Series", series.getTitle());
        }

        @Test
        @DisplayName("Should set and get description")
        void testSetDescription() {
            String description = "A captivating fantasy series spanning multiple kingdoms.";
            series.setDescription(description);
            assertEquals(description, series.getDescription());
        }

        @Test
        @DisplayName("Should set and get long description")
        void testSetLongDescription() {
            String longDescription = "A".repeat(2000);
            series.setDescription(longDescription);
            assertEquals(2000, series.getDescription().length());
        }

        @Test
        @DisplayName("Should set and get author")
        void testSetAuthor() {
            Author author = new Author();
            author.setId(1L);
            author.setFirstName("John");
            author.setLastName("Doe");

            series.setAuthor(author);

            assertEquals(author, series.getAuthor());
        }

        @Test
        @DisplayName("Should set and get books")
        void testSetBooks() {
            Set<Book> books = new LinkedHashSet<>();
            Book book1 = new Book();
            book1.setIsbn("9781234567890");
            Book book2 = new Book();
            book2.setIsbn("9781234567891");
            books.add(book1);
            books.add(book2);

            series.setBooks(books);

            assertEquals(2, series.getBooks().size());
        }

        @Test
        @DisplayName("Should set and get followers")
        void testSetFollowers() {
            Set<User> followers = new HashSet<>();
            User follower = new User();
            follower.setId(1L);
            followers.add(follower);

            series.setFollowers(followers);

            assertEquals(1, series.getFollowers().size());
            assertTrue(series.getFollowers().contains(follower));
        }
    }

    @Nested
    @DisplayName("Equals and HashCode tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when same id, title, description, author")
        void testEquals_SameFields_ReturnsTrue() {
            Author author = new Author();
            author.setId(1L);

            Series series1 = new Series();
            series1.setId(1L);
            series1.setTitle("Test Series");
            series1.setDescription("Description");
            series1.setAuthor(author);
            series1.setBooks(new LinkedHashSet<>());
            series1.setFollowers(new HashSet<>());

            Series series2 = new Series();
            series2.setId(1L);
            series2.setTitle("Test Series");
            series2.setDescription("Description");
            series2.setAuthor(author);
            series2.setBooks(null); // books excluded from equals
            series2.setFollowers(null); // followers excluded from equals

            assertEquals(series1, series2);
        }

        @Test
        @DisplayName("Should have same hashCode when equal")
        void testHashCode_EqualObjects_SameHashCode() {
            Series series1 = new Series();
            series1.setId(1L);
            series1.setTitle("Test Series");

            Series series2 = new Series();
            series2.setId(1L);
            series2.setTitle("Test Series");

            assertEquals(series1.hashCode(), series2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different id")
        void testEquals_DifferentId_ReturnsFalse() {
            Series series1 = new Series();
            series1.setId(1L);
            series1.setTitle("Test Series");

            Series series2 = new Series();
            series2.setId(2L);
            series2.setTitle("Test Series");

            assertNotEquals(series1, series2);
        }

        @Test
        @DisplayName("Should not be equal when different title")
        void testEquals_DifferentTitle_ReturnsFalse() {
            Series series1 = new Series();
            series1.setId(1L);
            series1.setTitle("Test Series");

            Series series2 = new Series();
            series2.setId(1L);
            series2.setTitle("Different Series");

            assertNotEquals(series1, series2);
        }

        @Test
        @DisplayName("Books and followers should be excluded from equals")
        void testEquals_DifferentBooksAndFollowers_StillEqual() {
            Set<Book> books1 = new LinkedHashSet<>();
            Book book = new Book();
            book.setIsbn("9781234567890");
            books1.add(book);

            Set<User> followers1 = new HashSet<>();
            User follower = new User();
            follower.setId(1L);
            followers1.add(follower);

            Series series1 = new Series();
            series1.setId(1L);
            series1.setTitle("Test Series");
            series1.setBooks(books1);
            series1.setFollowers(followers1);

            Series series2 = new Series();
            series2.setId(1L);
            series2.setTitle("Test Series");
            series2.setBooks(new LinkedHashSet<>()); // different books
            series2.setFollowers(new HashSet<>()); // different followers

            assertEquals(series1, series2);
        }
    }

    @Nested
    @DisplayName("ToString tests")
    class ToStringTests {

        @Test
        @DisplayName("Should not include books and followers in toString")
        void testToString_ExcludesBooksAndFollowers() {
            series.setId(1L);
            series.setTitle("Test Series");
            series.setDescription("Description");

            Set<Book> books = new LinkedHashSet<>();
            Book book = new Book();
            book.setIsbn("9781234567890");
            books.add(book);
            series.setBooks(books);

            String toString = series.toString();

            assertTrue(toString.contains("Test Series"));
            assertFalse(toString.contains("books"));
            assertFalse(toString.contains("followers"));
        }
    }

    @Nested
    @DisplayName("Relationship management tests")
    class RelationshipTests {

        @Test
        @DisplayName("Should add book to series")
        void testAddBook() {
            series.setBooks(new LinkedHashSet<>());
            Book book = new Book();
            book.setIsbn("9781234567890");

            series.getBooks().add(book);

            assertEquals(1, series.getBooks().size());
            assertTrue(series.getBooks().contains(book));
        }

        @Test
        @DisplayName("Should remove book from series")
        void testRemoveBook() {
            series.setBooks(new LinkedHashSet<>());
            Book book = new Book();
            book.setIsbn("9781234567890");
            series.getBooks().add(book);

            series.getBooks().remove(book);

            assertTrue(series.getBooks().isEmpty());
        }

        @Test
        @DisplayName("Should maintain book insertion order with LinkedHashSet")
        void testBookInsertionOrder() {
            series.setBooks(new LinkedHashSet<>());

            Book book1 = new Book();
            book1.setIsbn("9781234567890");
            book1.setTitle("First Book");

            Book book2 = new Book();
            book2.setIsbn("9781234567891");
            book2.setTitle("Second Book");

            Book book3 = new Book();
            book3.setIsbn("9781234567892");
            book3.setTitle("Third Book");

            series.getBooks().add(book1);
            series.getBooks().add(book2);
            series.getBooks().add(book3);

            Book[] booksArray = series.getBooks().toArray(new Book[0]);
            assertEquals("First Book", booksArray[0].getTitle());
            assertEquals("Second Book", booksArray[1].getTitle());
            assertEquals("Third Book", booksArray[2].getTitle());
        }

        @Test
        @DisplayName("Should add follower to series")
        void testAddFollower() {
            series.setFollowers(new HashSet<>());
            User follower = new User();
            follower.setId(1L);

            series.getFollowers().add(follower);

            assertEquals(1, series.getFollowers().size());
            assertTrue(series.getFollowers().contains(follower));
        }

        @Test
        @DisplayName("Should remove follower from series")
        void testRemoveFollower() {
            series.setFollowers(new HashSet<>());
            User follower = new User();
            follower.setId(1L);
            series.getFollowers().add(follower);

            series.getFollowers().remove(follower);

            assertTrue(series.getFollowers().isEmpty());
        }

        @Test
        @DisplayName("Should change author")
        void testChangeAuthor() {
            Author author1 = new Author();
            author1.setId(1L);
            Author author2 = new Author();
            author2.setId(2L);

            series.setAuthor(author1);
            assertEquals(author1, series.getAuthor());

            series.setAuthor(author2);
            assertEquals(author2, series.getAuthor());
        }
    }
}
