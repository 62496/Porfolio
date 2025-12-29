package com.prj2.booksta.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Nested
    @DisplayName("Constructor and Getter tests")
    class ConstructorAndGetterTests {

        @Test
        @DisplayName("Should create user with no-args constructor")
        void testNoArgsConstructor() {
            User newUser = new User();

            assertNull(newUser.getId());
            assertNull(newUser.getFirstName());
            assertNull(newUser.getLastName());
            assertNull(newUser.getEmail());
            assertNull(newUser.getPassword());
            assertNull(newUser.getGoogleId());
            assertNull(newUser.getPicture());
        }

        @Test
        @DisplayName("Should create user with all-args constructor")
        void testAllArgsConstructor() {
            Set<Book> favoriteList = new HashSet<>();
            Set<Author> followedAuthors = new HashSet<>();
            Set<Series> followedSeries = new HashSet<>();
            Set<Book> ownedBooks = new HashSet<>();
            Set<Role> roles = new HashSet<>();

            User newUser = new User(
                    1L,
                    "John",
                    "Doe",
                    "john@test.com",
                    "password123",
                    favoriteList,
                    followedAuthors,
                    followedSeries,
                    ownedBooks,
                    roles,
                    "google-id-123",
                    "http://example.com/pic.jpg"
            );

            assertEquals(1L, newUser.getId());
            assertEquals("John", newUser.getFirstName());
            assertEquals("Doe", newUser.getLastName());
            assertEquals("john@test.com", newUser.getEmail());
            assertEquals("password123", newUser.getPassword());
            assertSame(favoriteList, newUser.getFavoriteList());
            assertSame(followedAuthors, newUser.getFollowedAuthors());
            assertSame(followedSeries, newUser.getFollowedSeries());
            assertSame(ownedBooks, newUser.getOwnedBooks());
            assertSame(roles, newUser.getRoles());
            assertEquals("google-id-123", newUser.getGoogleId());
            assertEquals("http://example.com/pic.jpg", newUser.getPicture());
        }
    }

    @Nested
    @DisplayName("Setter tests")
    class SetterTests {

        @Test
        @DisplayName("Should set and get id")
        void testSetId() {
            user.setId(1L);
            assertEquals(1L, user.getId());
        }

        @Test
        @DisplayName("Should set and get firstName")
        void testSetFirstName() {
            user.setFirstName("John");
            assertEquals("John", user.getFirstName());
        }

        @Test
        @DisplayName("Should set and get lastName")
        void testSetLastName() {
            user.setLastName("Doe");
            assertEquals("Doe", user.getLastName());
        }

        @Test
        @DisplayName("Should set and get email")
        void testSetEmail() {
            user.setEmail("john@test.com");
            assertEquals("john@test.com", user.getEmail());
        }

        @Test
        @DisplayName("Should set and get password")
        void testSetPassword() {
            user.setPassword("securePassword123");
            assertEquals("securePassword123", user.getPassword());
        }

        @Test
        @DisplayName("Should set and get googleId")
        void testSetGoogleId() {
            user.setGoogleId("google-oauth-id-123");
            assertEquals("google-oauth-id-123", user.getGoogleId());
        }

        @Test
        @DisplayName("Should set and get picture")
        void testSetPicture() {
            user.setPicture("http://example.com/profile.jpg");
            assertEquals("http://example.com/profile.jpg", user.getPicture());
        }

        @Test
        @DisplayName("Should set and get favoriteList")
        void testSetFavoriteList() {
            Set<Book> favorites = new HashSet<>();
            Book book = new Book();
            book.setIsbn("9781234567890");
            favorites.add(book);

            user.setFavoriteList(favorites);

            assertEquals(1, user.getFavoriteList().size());
            assertTrue(user.getFavoriteList().contains(book));
        }

        @Test
        @DisplayName("Should set and get followedAuthors")
        void testSetFollowedAuthors() {
            Set<Author> authors = new HashSet<>();
            Author author = new Author();
            author.setId(1L);
            authors.add(author);

            user.setFollowedAuthors(authors);

            assertEquals(1, user.getFollowedAuthors().size());
            assertTrue(user.getFollowedAuthors().contains(author));
        }

        @Test
        @DisplayName("Should set and get followedSeries")
        void testSetFollowedSeries() {
            Set<Series> seriesSet = new HashSet<>();
            Series series = new Series();
            series.setId(1L);
            seriesSet.add(series);

            user.setFollowedSeries(seriesSet);

            assertEquals(1, user.getFollowedSeries().size());
            assertTrue(user.getFollowedSeries().contains(series));
        }

        @Test
        @DisplayName("Should set and get ownedBooks")
        void testSetOwnedBooks() {
            Set<Book> owned = new HashSet<>();
            Book book = new Book();
            book.setIsbn("9781234567890");
            owned.add(book);

            user.setOwnedBooks(owned);

            assertEquals(1, user.getOwnedBooks().size());
            assertTrue(user.getOwnedBooks().contains(book));
        }

        @Test
        @DisplayName("Should set and get roles")
        void testSetRoles() {
            Set<Role> roles = new HashSet<>();
            Role role = new Role();
            role.setId(1L);
            role.setName("USER");
            roles.add(role);

            user.setRoles(roles);

            assertEquals(1, user.getRoles().size());
            assertTrue(user.getRoles().contains(role));
        }
    }

    @Nested
    @DisplayName("Equals and HashCode tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when same core fields")
        void testEquals_SameFields_ReturnsTrue() {
            User user1 = new User();
            user1.setId(1L);
            user1.setFirstName("John");
            user1.setLastName("Doe");
            user1.setEmail("john@test.com");
            user1.setFavoriteList(new HashSet<>());
            user1.setFollowedAuthors(new HashSet<>());
            user1.setFollowedSeries(new HashSet<>());
            user1.setOwnedBooks(new HashSet<>());
            user1.setRoles(new HashSet<>());

            User user2 = new User();
            user2.setId(1L);
            user2.setFirstName("John");
            user2.setLastName("Doe");
            user2.setEmail("john@test.com");
            user2.setFavoriteList(null); // excluded from equals
            user2.setFollowedAuthors(null); // excluded from equals
            user2.setFollowedSeries(null); // excluded from equals
            user2.setOwnedBooks(null); // excluded from equals
            user2.setRoles(null); // excluded from equals

            assertEquals(user1, user2);
        }

        @Test
        @DisplayName("Should have same hashCode when equal")
        void testHashCode_EqualObjects_SameHashCode() {
            User user1 = new User();
            user1.setId(1L);
            user1.setFirstName("John");
            user1.setLastName("Doe");
            user1.setEmail("john@test.com");

            User user2 = new User();
            user2.setId(1L);
            user2.setFirstName("John");
            user2.setLastName("Doe");
            user2.setEmail("john@test.com");

            assertEquals(user1.hashCode(), user2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different id")
        void testEquals_DifferentId_ReturnsFalse() {
            User user1 = new User();
            user1.setId(1L);
            user1.setEmail("john@test.com");

            User user2 = new User();
            user2.setId(2L);
            user2.setEmail("john@test.com");

            assertNotEquals(user1, user2);
        }

        @Test
        @DisplayName("Should not be equal when different email")
        void testEquals_DifferentEmail_ReturnsFalse() {
            User user1 = new User();
            user1.setId(1L);
            user1.setEmail("john@test.com");

            User user2 = new User();
            user2.setId(1L);
            user2.setEmail("jane@test.com");

            assertNotEquals(user1, user2);
        }

        @Test
        @DisplayName("Collections should be excluded from equals")
        void testEquals_DifferentCollections_StillEqual() {
            Set<Book> favorites1 = new HashSet<>();
            Book book = new Book();
            book.setIsbn("9781234567890");
            favorites1.add(book);

            User user1 = new User();
            user1.setId(1L);
            user1.setFirstName("John");
            user1.setEmail("john@test.com");
            user1.setFavoriteList(favorites1);

            User user2 = new User();
            user2.setId(1L);
            user2.setFirstName("John");
            user2.setEmail("john@test.com");
            user2.setFavoriteList(new HashSet<>()); // different favorites

            assertEquals(user1, user2);
        }
    }

    @Nested
    @DisplayName("ToString tests")
    class ToStringTests {

        @Test
        @DisplayName("Should not include sensitive collections in toString")
        void testToString_ExcludesCollections() {
            user.setId(1L);
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setEmail("john@test.com");

            String toString = user.toString();

            assertTrue(toString.contains("John"));
            assertTrue(toString.contains("Doe"));
            assertFalse(toString.contains("favoriteList"));
            assertFalse(toString.contains("followedAuthors"));
            assertFalse(toString.contains("followedSeries"));
        }
    }

    @Nested
    @DisplayName("Favorites management tests")
    class FavoritesTests {

        @Test
        @DisplayName("Should add book to favorites")
        void testAddFavorite() {
            user.setFavoriteList(new HashSet<>());
            Book book = new Book();
            book.setIsbn("9781234567890");

            user.getFavoriteList().add(book);

            assertEquals(1, user.getFavoriteList().size());
            assertTrue(user.getFavoriteList().contains(book));
        }

        @Test
        @DisplayName("Should remove book from favorites")
        void testRemoveFavorite() {
            user.setFavoriteList(new HashSet<>());
            Book book = new Book();
            book.setIsbn("9781234567890");
            user.getFavoriteList().add(book);

            user.getFavoriteList().remove(book);

            assertTrue(user.getFavoriteList().isEmpty());
        }

        @Test
        @DisplayName("Should not add duplicate favorites")
        void testNoDuplicateFavorites() {
            user.setFavoriteList(new HashSet<>());
            Book book = new Book();
            book.setIsbn("9781234567890");

            user.getFavoriteList().add(book);
            user.getFavoriteList().add(book);

            assertEquals(1, user.getFavoriteList().size());
        }
    }

    @Nested
    @DisplayName("Following management tests")
    class FollowingTests {

        @Test
        @DisplayName("Should follow author")
        void testFollowAuthor() {
            user.setFollowedAuthors(new HashSet<>());
            Author author = new Author();
            author.setId(1L);

            user.getFollowedAuthors().add(author);

            assertEquals(1, user.getFollowedAuthors().size());
            assertTrue(user.getFollowedAuthors().contains(author));
        }

        @Test
        @DisplayName("Should unfollow author")
        void testUnfollowAuthor() {
            user.setFollowedAuthors(new HashSet<>());
            Author author = new Author();
            author.setId(1L);
            user.getFollowedAuthors().add(author);

            user.getFollowedAuthors().remove(author);

            assertTrue(user.getFollowedAuthors().isEmpty());
        }

        @Test
        @DisplayName("Should follow series")
        void testFollowSeries() {
            user.setFollowedSeries(new HashSet<>());
            Series series = new Series();
            series.setId(1L);

            user.getFollowedSeries().add(series);

            assertEquals(1, user.getFollowedSeries().size());
            assertTrue(user.getFollowedSeries().contains(series));
        }

        @Test
        @DisplayName("Should unfollow series")
        void testUnfollowSeries() {
            user.setFollowedSeries(new HashSet<>());
            Series series = new Series();
            series.setId(1L);
            user.getFollowedSeries().add(series);

            user.getFollowedSeries().remove(series);

            assertTrue(user.getFollowedSeries().isEmpty());
        }
    }

    @Nested
    @DisplayName("Owned books management tests")
    class OwnedBooksTests {

        @Test
        @DisplayName("Should add owned book")
        void testAddOwnedBook() {
            user.setOwnedBooks(new HashSet<>());
            Book book = new Book();
            book.setIsbn("9781234567890");

            user.getOwnedBooks().add(book);

            assertEquals(1, user.getOwnedBooks().size());
            assertTrue(user.getOwnedBooks().contains(book));
        }

        @Test
        @DisplayName("Should remove owned book")
        void testRemoveOwnedBook() {
            user.setOwnedBooks(new HashSet<>());
            Book book = new Book();
            book.setIsbn("9781234567890");
            user.getOwnedBooks().add(book);

            user.getOwnedBooks().remove(book);

            assertTrue(user.getOwnedBooks().isEmpty());
        }
    }

    @Nested
    @DisplayName("Role management tests")
    class RoleTests {

        @Test
        @DisplayName("Should add role to user")
        void testAddRole() {
            user.setRoles(new HashSet<>());
            Role role = new Role();
            role.setId(1L);
            role.setName("USER");

            user.getRoles().add(role);

            assertEquals(1, user.getRoles().size());
            assertTrue(user.getRoles().contains(role));
        }

        @Test
        @DisplayName("Should remove role from user")
        void testRemoveRole() {
            user.setRoles(new HashSet<>());
            Role role = new Role();
            role.setId(1L);
            role.setName("USER");
            user.getRoles().add(role);

            user.getRoles().remove(role);

            assertTrue(user.getRoles().isEmpty());
        }

        @Test
        @DisplayName("Should have multiple roles")
        void testMultipleRoles() {
            user.setRoles(new HashSet<>());

            Role userRole = new Role();
            userRole.setId(1L);
            userRole.setName("USER");

            Role authorRole = new Role();
            authorRole.setId(2L);
            authorRole.setName("AUTHOR");

            Role sellerRole = new Role();
            sellerRole.setId(3L);
            sellerRole.setName("SELLER");

            user.getRoles().add(userRole);
            user.getRoles().add(authorRole);
            user.getRoles().add(sellerRole);

            assertEquals(3, user.getRoles().size());
        }

        @Test
        @DisplayName("Should not add duplicate roles")
        void testNoDuplicateRoles() {
            user.setRoles(new HashSet<>());
            Role role = new Role();
            role.setId(1L);
            role.setName("USER");

            user.getRoles().add(role);
            user.getRoles().add(role);

            assertEquals(1, user.getRoles().size());
        }
    }
}
