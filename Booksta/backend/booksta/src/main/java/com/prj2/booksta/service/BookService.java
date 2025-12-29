package com.prj2.booksta.service;

import com.prj2.booksta.model.*;
import com.prj2.booksta.model.dto.BookFilterRequest;
import com.prj2.booksta.model.dto.UpdateBook;
import com.prj2.booksta.repository.*;
import static com.prj2.booksta.repository.BookSpecification.withFilters;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookReportRepository bookReportRepository;

    @Autowired
    private BookReadEventRepository bookReadEventRepository;

    @Autowired
    private ReadingSessionRepository readingSessionRepository;

    @Autowired
    private ReadingProgressRepository readingProgressRepository;

    @Autowired
    private UserBookInventoryRepository userBookInventoryRepository;

    @Autowired
    private BookCollectionRepository bookCollectionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Book> getAllBooks() {
        return (List<Book>) bookRepository.findAll();
    }

    public Book getBookByIsbn(String isbn) {
        return bookRepository.findById(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book with isbn not found: " + isbn));
    }

    public List<Book> searchBooks(String title, String authorName, String subjectName, Integer year) {
        return bookRepository.searchBooks(title, authorName, subjectName, year);
    }

    public List<Book> filterBooks(BookFilterRequest filter) {
        return bookRepository.findAll(withFilters(filter));
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public void delete(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + isbn));

        // 1. Delete book reports targeting this book
        bookReportRepository.deleteByBook_Isbn(isbn);

        // 2. Delete reading events
        bookReadEventRepository.deleteByBook_Isbn(isbn);

        // 3. Delete reading sessions
        readingSessionRepository.deleteByBook_Isbn(isbn);

        // 4. Delete reading progress
        readingProgressRepository.deleteByBook_Isbn(isbn);

        // 5. Delete inventory entries
        userBookInventoryRepository.deleteByBook_Isbn(isbn);

        // 6. Remove book from all collections
        List<BookCollection> collections = bookCollectionRepository.findByBooksIsbn(isbn);
        for (BookCollection collection : collections) {
            collection.getBooks().remove(book);
            bookCollectionRepository.save(collection);
        }

        // 7. Remove book from user favorites and owned books
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            boolean modified = false;
            if (user.getFavoriteList().remove(book)) {
                modified = true;
            }
            if (user.getOwnedBooks().remove(book)) {
                modified = true;
            }
            if (modified) {
                userRepository.save(user);
            }
        }

        // 8. Remove from series (don't delete the series)
        if (book.getSeries() != null) {
            book.setSeries(null);
        }

        // 9. Clear author and subject associations (don't delete them)
        book.getAuthors().clear();
        book.getSubjects().clear();
        bookRepository.save(book);

        // 10. Delete image if exists
        if (book.getImage() != null) {
            try {
                fileStorageService.deleteBookImage(isbn);
            } catch (Exception e) {
                // Log but don't fail if image deletion fails
            }
        }

        // 11. Delete the book
        bookRepository.delete(book);
    }

    public List<Book> findBySeriesId(Long seriesId) {
        return bookRepository.findBySeries_Id(seriesId);
    }

    public List<Book> findByAuthorId(Long authorId) {
        return bookRepository.findByAuthors_Id(authorId);
    }

    public Book updateBook(UpdateBook bookDto, String isbn, MultipartFile imageFile) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        book.setTitle(bookDto.bookTitle());
        book.setPublishingYear(bookDto.publishingYear());
        book.setDescription(bookDto.description());
        book.setPages(bookDto.pages());

        if (bookDto.authors() != null) {
            Set<Author> authors = new HashSet<>(authorService.findAllById(bookDto.authors()));
            book.setAuthors(authors);
        }

        if (bookDto.subjects() != null) {
            Set<Subject> subjects = new HashSet<>(subjectService.findAllById(bookDto.subjects()));
            book.setSubjects(subjects);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Image image = book.getImage();
                if (image == null) {
                    image = new Image();
                }
                image.setUrl(fileStorageService.saveBookImage(imageFile, isbn));
                imageService.createImage(image);
                book.setImage(image);
            } catch (IOException e) {
                throw new RuntimeException("Failed to update image", e);
            }
        } else if (bookDto.imageUrl() != null && !bookDto.imageUrl().isBlank()) {
            Image image = book.getImage();
            if (image == null) {
                image = new Image();
            }
            image.setUrl(bookDto.imageUrl());
            imageService.createImage(image);
            book.setImage(image);
        }
        return bookRepository.save(book);
    }

    public boolean isBookOwnedByAuthor(Long authorId, String isbn) {
        return bookRepository.findById(isbn)
                .map(book ->
                        book.getAuthors().stream()
                                .anyMatch(author -> author.getId().equals(authorId))
                )
                .orElse(false);
    }
}
