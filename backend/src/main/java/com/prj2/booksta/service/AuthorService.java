package com.prj2.booksta.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.Image;
import com.prj2.booksta.model.Series;
import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.AuthorDetailResponse;
import com.prj2.booksta.model.dto.BookSummary;
import com.prj2.booksta.repository.BookRepository;
import com.prj2.booksta.repository.SeriesRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj2.booksta.model.Author;
import com.prj2.booksta.repository.AuthorRepository;

@Service
public class AuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    public Iterable<Author> getAllAuthors() {
        return authorRepository.findAll();}

    
    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    public Author save(Author author) {
        return authorRepository.save(author);
    }

    public void addAuthor(User user) {
        // Check if author already exists for this user
        Optional<Author> existing = authorRepository.findByUser(user);
        if (existing.isPresent()) {
            return;
        }

        // Add AUTHOR role if it exists and user doesn't have it
        var authorRole = roleService.getRole("AUTHOR");
        if (authorRole != null && !user.getRoles().contains(authorRole)) {
            user.getRoles().add(authorRole);
            userService.save(user);
        }

        // Create author entity
        Author author = new Author();
        author.setUser(user);
        author.setFirstName(user.getFirstName());
        author.setLastName(user.getLastName());
        Image img = new Image(user.getPicture());
        author.setImage(img);
        imageService.createImage(img);
        authorRepository.save(author);
    }

    public @NotNull Set<Author> findAllById(List<Long> authors) {
        Iterable<Author> iterable = authorRepository.findAllById(authors);
        Set<Author> set = new HashSet<>();
        iterable.forEach(set::add);
        return set;
    }

    public Author findByUserId(Long userId) {
        return authorRepository.findByUser_Id(userId);
    }

    @Transactional(readOnly = true)
    public AuthorDetailResponse getAuthorDetails(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));

        List<Book> books = bookRepository.findByAuthors_Id(authorId);
        List<Series> seriesList = seriesRepository.findByAuthorId(authorId);

        AuthorDetailResponse response = new AuthorDetailResponse();
        response.setId(author.getId());
        response.setFirstName(author.getFirstName());
        response.setLastName(author.getLastName());
        response.setImageUrl(author.getImage() != null ? author.getImage().getUrl() : null);
        response.setFollowerCount(author.getFollowers() != null ? author.getFollowers().size() : 0);
        response.setBookCount(books.size());
        response.setSeriesCount(seriesList.size());

        List<BookSummary> bookSummaries = books.stream()
                .map(book -> new BookSummary(
                        book.getIsbn(),
                        book.getTitle(),
                        book.getPublishingYear(),
                        book.getImage() != null ? book.getImage().getUrl() : null
                ))
                .collect(Collectors.toList());
        response.setBooks(bookSummaries);

        List<AuthorDetailResponse.SeriesSummary> seriesSummaries = seriesList.stream()
                .map(series -> new AuthorDetailResponse.SeriesSummary(
                        series.getId(),
                        series.getTitle(),
                        series.getDescription(),
                        series.getBooks() != null ? series.getBooks().size() : 0,
                        series.getFollowers() != null ? series.getFollowers().size() : 0
                ))
                .collect(Collectors.toList());
        response.setSeries(seriesSummaries);

        return response;
    }
}
