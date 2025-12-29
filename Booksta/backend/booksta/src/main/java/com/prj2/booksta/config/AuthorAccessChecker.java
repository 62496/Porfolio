package com.prj2.booksta.config;

import com.prj2.booksta.repository.AuthorRepository;
import com.prj2.booksta.repository.BookRepository;
import com.prj2.booksta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component("authorAccessChecker")
public class AuthorAccessChecker {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BookRepository bookRepository;

    public boolean isAuthorOfBook(Authentication authentication, String isbn) {
        if (authentication == null || isbn == null) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            Long authenticatedUserId = userService.getUserByEmail(userDetails.getUsername()).getId();

            return bookRepository.findById(isbn)
                    .map(book -> book.getAuthors().stream()
                            .anyMatch(author -> author.getUser() != null
                                    && author.getUser().getId().equals(authenticatedUserId)))
                    .orElse(false);
        }

        return false;
    }
}
