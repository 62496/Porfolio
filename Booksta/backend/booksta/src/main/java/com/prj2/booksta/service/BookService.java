package com.prj2.booksta.service;

import com.prj2.booksta.model.Author;
import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.Image;
import com.prj2.booksta.model.Subject;
import com.prj2.booksta.model.dto.UpdateBook;
import com.prj2.booksta.repository.BookRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public void delete(String bookId) {
        bookRepository.deleteById(bookId);
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
