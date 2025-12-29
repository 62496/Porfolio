package com.prj2.booksta.controller;

import com.prj2.booksta.model.*;
import com.prj2.booksta.model.dto.BookFilterRequest;
import com.prj2.booksta.model.dto.CreateReadingEventRequest;
import com.prj2.booksta.model.dto.UpdateBook;
import com.prj2.booksta.repository.SeriesRepository;
import com.prj2.booksta.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private BookReadEventService bookReadEventService;

    @Autowired
    private ReadingSessionService readingSessionService;
    
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        Book book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) String subjectName,
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(bookService.searchBooks(title, authorName, subjectName, year));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Book>> filterBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer yearMin,
            @RequestParam(required = false) Integer yearMax,
            @RequestParam(required = false) Long pagesMin,
            @RequestParam(required = false) Long pagesMax,
            @RequestParam(required = false) List<Long> authorIds,
            @RequestParam(required = false) List<Long> subjectIds
    ) {
        BookFilterRequest filter = new BookFilterRequest(
                title, yearMin, yearMax, pagesMin, pagesMax, authorIds, subjectIds
        );
        return ResponseEntity.ok(bookService.filterBooks(filter));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Book> createBook(
            @RequestPart("book") Book book,
            @RequestPart("image") MultipartFile imageFile
    ) throws IOException {
        if (!imageFile.isEmpty()) {
            String path = fileStorageService.saveBookImage(imageFile, book.getIsbn());
            Image image = new Image();
            image.setUrl(path);
            Image savedImage = imageService.createImage(image);
            book.setImage(savedImage);
        }

        Book saved = bookService.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping(value = "/{isbn}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('LIBRARIAN') or (hasRole('AUTHOR') and @authorAccessChecker.isAuthorOfBook(authentication, #isbn))")
    public ResponseEntity<Book> updateBook(
            @RequestPart("book") UpdateBook bookDto,
            @RequestPart(name = "image", required = false) MultipartFile imageFile,
            @PathVariable String isbn
            ) {
        Book book = bookService.updateBook(bookDto, isbn, imageFile);
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{isbn}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        bookService.delete(isbn);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/series/{seriesId}")
    public ResponseEntity<List<Book>> getBooksBySeries(@PathVariable Long seriesId) {
        return ResponseEntity.ok(bookService.findBySeriesId(seriesId));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(bookService.findByAuthorId(authorId));
    }

    @PostMapping("/{isbn}/reports")
    public ResponseEntity<BookReport> reportBook(
            @RequestBody BookReport bookReport,
            @AuthenticationPrincipal UserDetails user,
            @PathVariable String isbn
    ) {
        bookReport.setUser(userService.getUserByEmail(user.getUsername()));
        bookReport.setBook(bookService.getBookByIsbn(isbn));

        BookReport saved = reportService.createBookReport(bookReport);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/{isbn}/read-events")
    @PreAuthorize("@bookSecurity.userOwnsBook(authentication, #isbn)")
    public ResponseEntity<BookReadEvent> createBookReadEvent(
            @RequestBody CreateReadingEventRequest eventType,
            @PathVariable String isbn,
            Authentication authentication
    ) {
        BookReadEvent event = bookReadEventService
                .createReadEvent(authentication.getName(), isbn, eventType.eventType());

        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @GetMapping("/{isbn}/read-event/latest")
    @PreAuthorize("@bookSecurity.userOwnsBook(authentication, #isbn)")
    public ResponseEntity<BookReadEvent> getLatestBookReadEvent(
            @PathVariable String isbn,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookReadEventService.getLatestReadEvent(authentication.getName(), isbn));
    }

    @GetMapping("/{isbn}/reading-sessions")
    @PreAuthorize("@bookSecurity.userOwnsBook(authentication, #isbn)")
    public ResponseEntity<List<ReadingSession>> getBookReadingSessions(
            @PathVariable String isbn,
            Authentication authentication
    ) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(readingSessionService.findByUserAndIsbn(user, isbn));
    }

    @GetMapping("/{isbn}/reading-events")
    @PreAuthorize("@bookSecurity.userOwnsBook(authentication, #isbn)")
    public ResponseEntity<List<BookReadEvent>> getBookReadEvents(
            @PathVariable String isbn,
            Authentication authentication
    ) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(bookReadEventService.findByUserAndIsbn(user.getId(), isbn));
    }
}
