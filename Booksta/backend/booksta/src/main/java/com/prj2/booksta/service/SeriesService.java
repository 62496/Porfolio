package com.prj2.booksta.service;

import com.prj2.booksta.model.Author;
import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.Series;
import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.BookSummary;
import com.prj2.booksta.model.dto.SeriesRequest;
import com.prj2.booksta.model.dto.SeriesResponse;
import com.prj2.booksta.repository.AuthorRepository;
import com.prj2.booksta.repository.BookRepository;
import com.prj2.booksta.repository.SeriesRepository;
import com.prj2.booksta.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeriesService {

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    private User getAuthenticatedUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private Author getAuthorForUser(User user) {
        return authorRepository.findByUser(user)
                .orElseThrow(() -> new AccessDeniedException("User is not an author"));
    }

    private boolean isLibrarian() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"));
    }

    private void validateSeriesOwnership(Series series) {
        // Librarians can modify any series
        if (isLibrarian()) {
            return;
        }
        User user = getAuthenticatedUser();
        Author author = getAuthorForUser(user);
        if (series.getAuthor() == null || !series.getAuthor().getId().equals(author.getId())) {
            throw new AccessDeniedException("You are not the author of this series");
        }
    }

    private SeriesResponse toResponse(Series series) {
        SeriesResponse response = new SeriesResponse();
        response.setId(series.getId());
        response.setTitle(series.getTitle());
        response.setDescription(series.getDescription());
        response.setBookCount(series.getBooks() != null ? series.getBooks().size() : 0);
        response.setFollowerCount(series.getFollowers() != null ? series.getFollowers().size() : 0);

        if (series.getAuthor() != null) {
            Author author = series.getAuthor();
            SeriesResponse.AuthorSummary authorSummary = new SeriesResponse.AuthorSummary();
            authorSummary.setId(author.getId());
            authorSummary.setFirstName(author.getFirstName());
            authorSummary.setLastName(author.getLastName());
            authorSummary.setImageUrl(author.getImage() != null ? author.getImage().getUrl() : null);
            response.setAuthor(authorSummary);
        }

        return response;
    }

    private BookSummary toBookSummary(Book book) {
        return new BookSummary(
                book.getIsbn(),
                book.getTitle(),
                book.getPublishingYear(),
                book.getImage() != null ? book.getImage().getUrl() : null
        );
    }

    @Transactional(readOnly = true)
    public List<SeriesResponse> getAllSeries() {
        return seriesRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SeriesResponse> getSeriesByAuthorId(Long authorId) {
        return seriesRepository.findByAuthorId(authorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SeriesResponse getSeriesById(Long id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Series not found"));
        return toResponse(series);
    }

    @Transactional
    public SeriesResponse createSeries(SeriesRequest request) {
        Author author;

        // If librarian and authorId is provided, use that author
        if (isLibrarian() && request.getAuthorId() != null) {
            author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new EntityNotFoundException("Author not found: " + request.getAuthorId()));
        } else {
            // Otherwise, get the author for the authenticated user
            User user = getAuthenticatedUser();
            author = getAuthorForUser(user);
        }

        Series series = new Series();
        series.setTitle(request.getTitle());
        series.setDescription(request.getDescription());
        series.setAuthor(author);

        Series saved = seriesRepository.save(series);
        return toResponse(saved);
    }

    @Transactional
    public SeriesResponse updateSeries(Long id, SeriesRequest request) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Series not found"));
        validateSeriesOwnership(series);

        if (request.getTitle() != null) {
            series.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            series.setDescription(request.getDescription());
        }

        Series saved = seriesRepository.save(series);
        return toResponse(saved);
    }

    @Transactional
    public void deleteSeries(Long id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Series not found"));
        validateSeriesOwnership(series);

        // Remove books from series
        for (Book book : series.getBooks()) {
            book.setSeries(null);
            bookRepository.save(book);
        }

        // Remove followers (don't delete users, just the follow relationship)
        for (User follower : series.getFollowers()) {
            follower.getFollowedSeries().remove(series);
            userRepository.save(follower);
        }

        seriesRepository.delete(series);
    }

    @Transactional
    public SeriesResponse addBookToSeries(Long seriesId, String isbn) {
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new EntityNotFoundException("Series not found"));
        validateSeriesOwnership(series);

        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        book.setSeries(series);
        bookRepository.save(book);

        return toResponse(seriesRepository.findById(seriesId).get());
    }

    @Transactional
    public SeriesResponse removeBookFromSeries(Long seriesId, String isbn) {
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new EntityNotFoundException("Series not found"));
        validateSeriesOwnership(series);

        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        if (book.getSeries() != null && book.getSeries().getId().equals(seriesId)) {
            book.setSeries(null);
            bookRepository.save(book);
        }

        return toResponse(seriesRepository.findById(seriesId).get());
    }

    @Transactional(readOnly = true)
    public List<BookSummary> getSeriesBooks(Long seriesId) {
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new EntityNotFoundException("Series not found"));
        return series.getBooks().stream()
                .map(this::toBookSummary)
                .collect(Collectors.toList());
    }

    public boolean isAuthorOfSeries(String email, Long seriesId) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return false;

        Author author = authorRepository.findByUser(user).orElse(null);
        if (author == null) return false;

        Series series = seriesRepository.findById(seriesId).orElse(null);
        if (series == null || series.getAuthor() == null) return false;

        return series.getAuthor().getId().equals(author.getId());
    }
}
