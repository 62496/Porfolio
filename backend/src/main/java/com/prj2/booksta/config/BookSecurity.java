package com.prj2.booksta.config;

import com.prj2.booksta.model.BookReadEvent;
import com.prj2.booksta.model.ReadingEventType;
import com.prj2.booksta.service.BookReadEventService;
import com.prj2.booksta.service.BookService;
import com.prj2.booksta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component(value = "bookSecurity")
public class BookSecurity {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookReadEventService bookReadEventService;

    public boolean userOwnsBook(Authentication authentication, String isbn) {
        String username = authentication.getName();

        System.out.println("here is his name: " + username);

        return userService.userOwnsBook(username, isbn);
    }

    public boolean userReadsBook(Authentication authentication, String isbn) {
        BookReadEvent bookReadEvent = bookReadEventService.getLatestReadEvent(authentication.getName(), isbn);
        return bookReadEvent != null &&
                (bookReadEvent.getReadingEvent() == ReadingEventType.STARTED_READING || bookReadEvent.getReadingEvent() == ReadingEventType.RESTARTED_READING);
    }
}
