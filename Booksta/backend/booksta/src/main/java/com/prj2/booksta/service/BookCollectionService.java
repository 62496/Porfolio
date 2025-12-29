package com.prj2.booksta.service;

import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.BookCollection;
import com.prj2.booksta.model.CollectionVisibility;
import com.prj2.booksta.model.Image;
import com.prj2.booksta.model.User;
import com.prj2.booksta.repository.BookCollectionRepository;
import com.prj2.booksta.repository.BookRepository;
import com.prj2.booksta.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class BookCollectionService {

        @Autowired
        private BookCollectionRepository repo;
        @Autowired
        private UserRepository userRepo;
        @Autowired
        private BookRepository bookRepo;
        @Autowired
        private FileStorageService fileStorageService;
        @Autowired
        private ImageService imageService;

        private User getAuthenticatedUser() {
                String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                                .getUsername();
                return userRepo.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        }

        private void validateOwnership(BookCollection collection) {
                if (!collection.getOwner().getId().equals(getAuthenticatedUser().getId())) {
                        throw new AccessDeniedException("You are not the owner of this collection");
                }
        }

        /** create collection */
        public BookCollection createCollection(BookCollection collection, MultipartFile image) {
                collection.setOwner(getAuthenticatedUser());
                if (collection.getVisibility() == null) {
                        collection.setVisibility(CollectionVisibility.PRIVATE);
                }

                BookCollection saved = repo.save(collection);

                if (image != null && !image.isEmpty()) {
                        try {
                                String imageUrl = fileStorageService.saveCollectionImage(image, saved.getId());
                                Image img = imageService.createImage(new Image(imageUrl));
                                saved.setImage(img);
                                saved = repo.save(saved);
                        } catch (IOException e) {
                                throw new RuntimeException("Failed to save collection image", e);
                        }
                }

                return saved;
        }

        /** Delete a own collection */
        public void deleteCollection(Long collectionId) {
                BookCollection collection = repo.findById(collectionId)
                                .orElseThrow(() -> new EntityNotFoundException("Collection not found"));
                validateOwnership(collection);

                try {
                        fileStorageService.deleteCollectionImage(collectionId);
                } catch (IOException e) {
                        // Log but don't fail if image deletion fails
                }

                repo.deleteById(collectionId);
        }

        /** update collection */
        public BookCollection updateCollection(Long collectionId, BookCollection updates, MultipartFile image) {
                BookCollection collection = repo.findById(collectionId)
                                .orElseThrow(() -> new EntityNotFoundException("Collection not found"));
                validateOwnership(collection);

                if (updates.getName() != null) {
                        collection.setName(updates.getName());
                }
                if (updates.getDescription() != null) {
                        collection.setDescription(updates.getDescription());
                }
                if (updates.getVisibility() != null) {
                        collection.setVisibility(updates.getVisibility());
                }

                if (image != null && !image.isEmpty()) {
                        try {
                                String imageUrl = fileStorageService.saveCollectionImage(image, collectionId);
                                if (collection.getImage() != null) {
                                        collection.getImage().setUrl(imageUrl);
                                } else {
                                        Image img = imageService.createImage(new Image(imageUrl));
                                        collection.setImage(img);
                                }
                        } catch (IOException e) {
                                throw new RuntimeException("Failed to save collection image", e);
                        }
                }

                return repo.save(collection);
        }

        /** fetch a collection if user can access (owner, shared, or public) */
        public Optional<BookCollection> getCollectionIfAllowed(Long collectionId) {
                Long userId = getAuthenticatedUser().getId();
                if (repo.canUserAccess(collectionId, userId)) {
                        return repo.findById(collectionId);
                }
                return Optional.empty();
        }

        /** fetch all collections owned by authenticated user */
        public List<BookCollection> getAllOwnCollections() {
                return repo.findByOwnerId(getAuthenticatedUser().getId());
        }

        /** fetch all collections user can access: own, shared, and public */
        public List<BookCollection> getAllCollectionsAllowed() {
                return repo.findAccessibleByUser(getAuthenticatedUser().getId());
        }

        /** fetch all public collections */
        public List<BookCollection> getAllPublicCollections() {
                return repo.findAllPublic();
        }

        /** fetch collections shared with authenticated user */
        public List<BookCollection> getSharedWithMe() {
                return repo.findSharedWithUser(getAuthenticatedUser().getId());
        }

        /** share a own collection with another user (only for private collections) */
        public BookCollection shareWith(Long collectionId, String userEmail) {
                BookCollection c = repo.findById(collectionId)
                                .orElseThrow(() -> new EntityNotFoundException("Collection not found"));
                validateOwnership(c);

                User u = userRepo.findByEmail(userEmail)
                                .orElseThrow(() -> new EntityNotFoundException("User not found"));

                c.getSharedWith().add(u);
                return repo.save(c);
        }

        /** unshare a own collection from another user */
        public BookCollection unshareWith(Long collectionId, Long userId) {
                BookCollection c = repo.findById(collectionId)
                                .orElseThrow(() -> new EntityNotFoundException("Collection not found"));
                validateOwnership(c);

                c.getSharedWith().removeIf(u -> u.getId().equals(userId));
                return repo.save(c);
        }

        /** add a book to a own collection */
        public BookCollection addBook(Long collectionId, String isbn) {
                BookCollection c = repo.findById(collectionId)
                                .orElseThrow(() -> new EntityNotFoundException("Collection not found"));
                validateOwnership(c);

                Book b = bookRepo.findById(isbn)
                                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

                User user = getAuthenticatedUser();
                boolean ownsBook = user.getOwnedBooks().stream()
                                .anyMatch(book -> book.getIsbn().equals(isbn));
                if (!ownsBook) {
                        throw new AccessDeniedException("You must own the book to add it to a collection");
                }

                c.getBooks().add(b);
                return repo.save(c);
        }

        /** remove a book from a own collection */
        public BookCollection removeBook(Long collectionId, String isbn) {
                BookCollection c = repo.findById(collectionId)
                                .orElseThrow(() -> new EntityNotFoundException("Collection not found"));
                validateOwnership(c);

                c.getBooks().removeIf(book -> book.getIsbn().equals(isbn));
                return repo.save(c);
        }

        /** Check if the collection already contains the book */
        public boolean collectionContainsBook(Long collectionId, String isbn) {
                return repo.findById(collectionId)
                                .map(collection -> collection.getBooks()
                                                .stream()
                                                .anyMatch(book -> book.getIsbn().equals(isbn)))
                                .orElse(false);
        }

        /** Check if user can access a collection */
        public boolean canAccess(Long collectionId) {
                return repo.canUserAccess(collectionId, getAuthenticatedUser().getId());
        }
}
