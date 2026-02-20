package com.prj2.booksta.controller;
import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.BookWithLatestReadingEvent;
import com.prj2.booksta.service.BookService;
import com.prj2.booksta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/me")
@CrossOrigin("*")
public class MeController {
    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    /**
     * GET /api/me/books
     * Returns all owned books with latest reading event
     */
    @GetMapping("/books")
    public ResponseEntity<List<BookWithLatestReadingEvent>> getOwnedBooks(
            Authentication authentication
    ) {
        User user = userService.getUserByEmail(authentication.getName());

        return ResponseEntity.ok(
                userService.getOwnedBooksWithLatestReadingEvent(user.getId())
        );
    }

    @GetMapping("/reading-progress/books")
    public ResponseEntity<List<BookWithLatestReadingEvent>> getOwnedBooksWithProgress(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());

        return ResponseEntity.ok(
                userService.getOwnedBooksWithReadingEvent(user.getId())
        );
    }
}
