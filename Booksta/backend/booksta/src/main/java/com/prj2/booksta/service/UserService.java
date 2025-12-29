package com.prj2.booksta.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.prj2.booksta.model.*;
import com.prj2.booksta.model.dto.BookReadingEvent;
import com.prj2.booksta.model.dto.BookWithLatestReadingEvent;
import com.prj2.booksta.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private SeriesRepository seriesRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private ImageRepository imageRepository;
    @Autowired private UserBookInventoryRepository userBookInventoryRepository;
    @Autowired @Lazy private AuthorService authorService;

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Utilisateur non trouvé (id = " + userId + ")"));
    }

    private Book getBookOrThrow(String bookIsbn) {
        return bookRepository.findById(bookIsbn)
                .orElseThrow(() ->
                        new IllegalArgumentException("Livre non trouvé (isbn = " + bookIsbn + ")"));
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    // Used by Book Reports
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + email));
    }

    @Transactional(readOnly = true)
    public User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Utilisateur non trouvé avec l'email : " + email));
    }

    @Transactional
    public void followAuthor(Long userId, Long authorId) {
        User user = getUserOrThrow(userId);
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Author not found with id " + authorId));

        if (!user.getFollowedAuthors().contains(author)) {
            user.getFollowedAuthors().add(author);
        }
    }

    @Transactional
    public void unfollowAuthor(Long userId, Long authorId) {
        User user = getUserOrThrow(userId);
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Author not found with id " + authorId));

        if (user.getFollowedAuthors().remove(author)) {
            userRepository.save(user);
        }
    }

    public boolean isFollowingAuthor(Long userId, Long authorId) {
        return userRepository.findById(userId)
                .map(user -> user.getFollowedAuthors()
                        .stream()
                        .anyMatch(a -> a.getId().equals(authorId)))
                .orElse(false);
    }

    public Set<Author> getFollowedAuthors(Long userId) {
        return userRepository.findById(userId)
                .map(User::getFollowedAuthors)
                .orElse(Set.of());
    }

    @Transactional
    public void followSeries(Long userId, Long seriesId) {
        User user = getUserOrThrow(userId);
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Series not found with id " + seriesId));

        if (!user.getFollowedSeries().contains(series)) {
            user.getFollowedSeries().add(series);
        }
    }

    @Transactional
    public void unfollowSeries(Long userId, Long seriesId) {
        User user = getUserOrThrow(userId);
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Series not found with id " + seriesId));

        if (user.getFollowedSeries().remove(series)) {
            userRepository.save(user);
        }
    }

    public boolean isFollowingSeries(Long userId, Long seriesId) {
        return userRepository.findById(userId)
                .map(user -> user.getFollowedSeries()
                        .stream()
                        .anyMatch(s -> s.getId().equals(seriesId)))
                .orElse(false);
    }

    public Set<Series> getFollowedSeries(Long userId) {
        return userRepository.findById(userId)
                .map(User::getFollowedSeries)
                .orElse(Set.of());
    }

    @Transactional
    public void addFavorite(Long userId, String bookIsbn) {
        User user = getUserOrThrow(userId);
        Book book = getBookOrThrow(bookIsbn);

        if (user.getFavoriteList().contains(book)) {
            throw new IllegalArgumentException("Ce livre est déjà dans vos favoris");
        }

        user.getFavoriteList().add(book);
        userRepository.save(user);
    }

    @Transactional
    public void removeFavorite(Long userId, String bookIsbn) {
        User user = getUserOrThrow(userId);
        Book book = getBookOrThrow(bookIsbn);

        if (!user.getFavoriteList().contains(book)) {
            throw new IllegalArgumentException("Ce livre n'est pas dans vos favoris");
        }

        user.getFavoriteList().remove(book);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Set<Book> getFavorites(Long userId) {
        return getUserOrThrow(userId).getFavoriteList();
    }

    @Transactional(readOnly = true)
    public Set<Book> getFavoritesOptimized(Long userId) {
        User user = userRepository.findByIdWithFavorites(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Utilisateur non trouvé (id = " + userId + ")"));

        return user.getFavoriteList();
    }

    @Transactional(readOnly = true)
    public List<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return userRepository.searchByNameOrEmail(query.trim());
    }

    @Transactional(readOnly = true)
    public List<User> searchGoogleUsers(String query, Long excludeId) {
        String sanitized =
                (query == null || query.trim().isEmpty()) ? null : query.trim();

        return userRepository.searchGoogleUsers(sanitized, excludeId);
    }

    @Transactional(readOnly = true)
    public List<User> getAllGoogleUsers(Long excludeId) {
        return userRepository.searchGoogleUsers(null, excludeId);
    }

    @Transactional
    public User findOrCreateGoogleUser(
            String email,
            String firstName,
            String lastName,
            String googleId,
            String picture
    ) {
        return userRepository.findByGoogleId(googleId)
                .orElseGet(() ->
                        userRepository.findByEmail(email)
                                .orElseGet(() -> {
                                    // Check if any user already has ADMIN role
                                    boolean noAdminExists = !userRepository.existsUserWithRole("ADMIN");

                                    User newUser = new User();
                                    newUser.setEmail(email);
                                    newUser.setFirstName(firstName);
                                    newUser.setLastName(lastName);
                                    newUser.setGoogleId(googleId);
                                    newUser.setPicture(picture);

                                    // Assign USER role to all new users
                                    Role userRole = roleRepository.findByName("USER");
                                    if (userRole != null) {
                                        newUser.getRoles().add(userRole);
                                    }

                                    // Assign ADMIN role if no admin exists yet
                                    if (noAdminExists) {
                                        Role adminRole = roleRepository.findByName("ADMIN");
                                        if (adminRole != null) {
                                            newUser.getRoles().add(adminRole);
                                        }
                                    }

                                    return userRepository.save(newUser);
                                })
                );
    }

      /**
     * Récupère les livres possédés (chargement lazy)
     */
      @Transactional(readOnly = true)
      public Set<Book> getOwnedBooks(Long userId) {
          return getUserOrThrow(userId).getOwnedBooks();
      }
  
      /**
       * Récupère les livres possédés en une seule requête (JOIN FETCH)
       */
      @Transactional(readOnly = true)
      public Set<Book> getOwnedBooksOptimized(Long userId) {
          User user = userRepository.findByIdWithOwnedBooks(userId)
                  .orElseThrow(() ->
                          new IllegalArgumentException("Utilisateur non trouvé (id = " + userId + ")"));
  
          return user.getOwnedBooks();
      }
  
      /**
       * Ajoute un livre à la bibliothèque personnelle
       */
      @Transactional
      public void addOwnedBook(Long userId, String bookIsbn) {
          User user = getUserOrThrow(userId);
          Book book = getBookOrThrow(bookIsbn);
  
          if (user.getOwnedBooks().contains(book)) {
              throw new IllegalArgumentException("Ce livre est déjà dans votre bibliothèque personnelle");
          }
  
          user.getOwnedBooks().add(book);
          userRepository.save(user);
      }
  
      /**
       * Retire un livre de la bibliothèque personnelle
       */
      @Transactional
      public void removeOwnedBook(Long userId, String bookIsbn) {
          User user = getUserOrThrow(userId);
          Book book = getBookOrThrow(bookIsbn);
  
          if (!user.getOwnedBooks().contains(book)) {
              throw new IllegalArgumentException("Ce livre n'est pas dans votre bibliothèque personnelle");
          }
  
          user.getOwnedBooks().remove(book);
          userRepository.save(user);
      }

    public boolean userOwnsBook(String email, String isbn) {
        return userRepository.findByEmail(email)
                .map(user ->
                        user.getOwnedBooks().stream()
                                .anyMatch(book -> book.getIsbn().equals(isbn))
                )
                .orElse(false);
    }

    public List<BookWithLatestReadingEvent> getOwnedBooksWithLatestReadingEvent(Long id) {
        return userRepository.findOwnedBooksWithLatestReadingEventView(id)
                .stream()
                .map(v -> new BookWithLatestReadingEvent(
                        v.getIsbn(),
                        v.getTitle(),
                        v.getPublishingYear(),
                        v.getPages(),
                        v.getImageUrl(),
                        v.getLatestEventType() == null
                                ? null
                                : new BookReadingEvent(
                                v.getLatestEventType(),
                                v.getLatestEventOccurredAt()
                        )
                ))
                .toList();
    }

    public List<BookWithLatestReadingEvent> getOwnedBooksWithReadingEvent(Long id) {
          return userRepository.findOwnedBooksWithReadingEvent(id)
                  .stream()
                  .map(v -> new BookWithLatestReadingEvent(
                          v.getIsbn(),
                          v.getTitle(),
                          v.getPublishingYear(),
                          v.getPages(),
                          v.getImageUrl(),
                          new BookReadingEvent(v.getLatestEventType(), v.getLatestEventOccurredAt())
                  )).toList();
    }

    // ==================== ADMIN METHODS ====================

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void addRoleToUser(Long userId, String roleName) {
        User user = getUserOrThrow(userId);
        Role role = findRoleByName(roleName);

        if (role == null) {
            throw new IllegalArgumentException("Role not found: " + roleName);
        }

        if (user.getRoles().stream().anyMatch(r -> r.getName().equals(roleName))) {
            throw new IllegalArgumentException("User already has role: " + roleName);
        }

        if (role.getName().equals("AUTHOR")) {
            // Check if author already exists for this user
            Author existingAuthor = authorRepository.findByUser_Id(userId);
            if (existingAuthor == null) {
                Author author = new Author();
                author.setUser(user);
                author.setFirstName(user.getFirstName());
                author.setLastName(user.getLastName());
                Image image = new Image();
                image.setUrl(user.getPicture());
                imageRepository.save(image);
                author.setImage(image);
                authorRepository.save(author);
            }
        }

        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Transactional
    public void removeRoleFromUser(Long userId, String roleName) {
        User user = getUserOrThrow(userId);

        boolean removed = user.getRoles().removeIf(r -> r.getName().equals(roleName));

        if (!removed) {
            throw new IllegalArgumentException("User does not have role: " + roleName);
        }

        // If removing AUTHOR role, also delete the Author entity with full cascade
        if (roleName.equals("AUTHOR")) {
            Author author = authorRepository.findByUser_Id(userId);
            if (author != null) {
                authorService.deleteAuthor(author.getId());
            }
        }

        // If removing SELLER role, delete all user's inventory/listings
        if (roleName.equals("SELLER")) {
            userBookInventoryRepository.deleteByUserId(userId);
        }

        userRepository.save(user);
    }

    private Role findRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}