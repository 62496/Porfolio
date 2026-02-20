package com.prj2.booksta.controller;

import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.ReadingSession;
import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.ReadingSessionCreate;
import com.prj2.booksta.model.dto.ReadingSessionUpdate;
import com.prj2.booksta.service.BookService;
import com.prj2.booksta.service.ReadingSessionService;
import com.prj2.booksta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/reading-sessions")
@CrossOrigin("*")
public class ReadingSessionController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReadingSessionService readingSessionService;

    @Autowired
    private BookService bookService;

    @PostMapping
    @PreAuthorize("@bookSecurity.userReadsBook(authentication, #readingSession.isbn())")
    public ResponseEntity<ReadingSession> startReadingSession(
            @RequestBody ReadingSessionCreate readingSession,
            Authentication authentication
    ) {
        User user = userService.getUserByEmail(authentication.getName());
        Book book = bookService.getBookByIsbn(readingSession.isbn());

        return ResponseEntity.ok(
                readingSessionService.createSession(
                        user,
                        book,
                        readingSession.startPage()
                )
        );
    }

    @PutMapping("/{id}/pause")
    public ResponseEntity<ReadingSession> pauseReadingSession(
            @PathVariable Long id,
            Authentication authentication
    ) throws AccessDeniedException {

        User user = userService.getUserByEmail(authentication.getName());

        return ResponseEntity.ok(
                readingSessionService.pauseReadingSession(user, id)
        );
    }

    @PutMapping("/{id}/resume")
    public ResponseEntity<ReadingSession> resumeReadingSession(
            @PathVariable Long id,
            Authentication authentication
    ) throws AccessDeniedException {

        User user = userService.getUserByEmail(authentication.getName());

        return ResponseEntity.ok(
                readingSessionService.resumeReadingSession(user, id)
        );
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<ReadingSession> endReadingSession(
            @PathVariable Long id,
            @RequestBody ReadingSessionUpdate readingSession,
            Authentication authentication
    ) throws AccessDeniedException {

        User user = userService.getUserByEmail(authentication.getName());

        return ResponseEntity.ok(
                readingSessionService.endReadingSession(
                        user,
                        id,
                        readingSession.startPage(),
                        readingSession.endPage(),
                        readingSession.note()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id
    ) throws AccessDeniedException {

        readingSessionService.deleteSession(id);
        return ResponseEntity.ok("Successfully deleted");
    }
}
