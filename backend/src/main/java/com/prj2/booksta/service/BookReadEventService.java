package com.prj2.booksta.service;

import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.BookReadEvent;
import com.prj2.booksta.model.ReadingEventType;
import com.prj2.booksta.model.User;
import com.prj2.booksta.exception.InvalidReadingEventTransitionException;
import com.prj2.booksta.repository.BookReadEventRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class BookReadEventService {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookReadEventRepository bookReadEventRepository;

    @Transactional
    public BookReadEvent createReadEvent(String email, String isbn, ReadingEventType eventType) {
        User user = userService.getUserByEmail(email);
        Book book = bookService.getBookByIsbn(isbn);

        Optional<BookReadEvent> latestReadEvent = bookReadEventRepository.findTopByUserAndBookOrderByOccurredAtDesc(user, book);

        if (!isValidTransition(latestReadEvent.map(BookReadEvent::getReadingEvent).orElse(null), eventType)) {
            throw new InvalidReadingEventTransitionException("Invalid reading event transition");
        }

        BookReadEvent event = new BookReadEvent();
        event.setUser(user);
        event.setBook(book);
        event.setReadingEvent(eventType);
        event.setOccurredAt(Instant.now());

        return bookReadEventRepository.save(event);
    }

    public BookReadEvent getLatestReadEvent(String email, String isbn) {
        User user = userService.getUserByEmail(email);
        Book book = bookService.getBookByIsbn(isbn);
        return bookReadEventRepository.findTopByUserAndBookOrderByOccurredAtDesc(user, book).orElse(null);
    }

    private boolean isValidTransition(ReadingEventType last, ReadingEventType next) {

        if (last == null) {
            return next == ReadingEventType.STARTED_READING;
        }

        return switch (last) {
            case STARTED_READING, RESTARTED_READING ->
                    next == ReadingEventType.FINISHED_READING
                            || next == ReadingEventType.ABANDONED_READING;

            case FINISHED_READING, ABANDONED_READING ->
                    next == ReadingEventType.RESTARTED_READING;
        };
    }

    public List<BookReadEvent> findByUserAndIsbn(Long id, String isbn) {
        return bookReadEventRepository.findByUser_IdAndBook_IsbnOrderByOccurredAtDesc(id, isbn);
    }
}
