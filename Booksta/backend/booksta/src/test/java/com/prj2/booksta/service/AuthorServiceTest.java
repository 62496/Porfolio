package com.prj2.booksta.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.prj2.booksta.model.*;
import com.prj2.booksta.model.dto.AuthorDetailResponse;
import com.prj2.booksta.repository.BookRepository;
import com.prj2.booksta.repository.SeriesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prj2.booksta.repository.AuthorRepository;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private UserService userService;

    @Mock
    private ImageService imageService;
    
    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private SeriesRepository seriesRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author author1;
    private Author author2;
    private User user;

    @BeforeEach
    void setUp() {
        author1 = new Author();
        author1.setId(1L);
        author1.setFirstName("James");
        author1.setLastName("Doe");

        author2 = new Author();
        author2.setId(2L);
        author2.setFirstName("John");
        author2.setLastName("Smith");

        user = new User();
        user.setId(10L);
        user.setFirstName("New");
        user.setLastName("Writer");
        user.setRoles(new HashSet<>());
    }

    @Test
    void testGetAllAuthors() {
        List<Author> authors = Arrays.asList(author1, author2);
        when(authorRepository.findAll()).thenReturn(authors);

        Iterable<Author> result = authorService.getAllAuthors();

        assertNotNull(result);
        List<Author> resultList = new ArrayList<>();
        result.forEach(resultList::add);
        
        assertEquals(2, resultList.size());
        verify(authorRepository).findAll();
    }

    @Test
    void testGetAuthorByIdFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));

        Optional<Author> result = authorService.getAuthorById(1L);

        assertTrue(result.isPresent());
        assertEquals("James", result.get().getFirstName());
        verify(authorRepository).findById(1L);
    }

    @Test
    void testGetAuthorByIdNotFound() {
        when(authorRepository.findById(3L)).thenReturn(Optional.empty());

        Optional<Author> result = authorService.getAuthorById(3L);

        assertFalse(result.isPresent());
        verify(authorRepository).findById(3L);
    }

    @Test
    void testSaveAuthor() {
        when(authorRepository.save(author1)).thenReturn(author1);

        Author saved = authorService.save(author1);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        verify(authorRepository).save(author1);
    }

    @Test
    void testAddAuthor_NewAuthor_CreatesAndSaves() {
        Role authorRole = new Role();
        authorRole.setName("AUTHOR");
        
        when(roleService.getRole("AUTHOR")).thenReturn(authorRole);
        when(authorRepository.findByUser(user)).thenReturn(Optional.empty());
        when(imageService.createImage(any(Image.class))).thenReturn(new Image());

        authorService.addAuthor(user);

        assertTrue(user.getRoles().contains(authorRole));

        verify(userService).save(user);
        verify(imageService).createImage(any(Image.class));
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void testAddAuthor_ExistingAuthor_DoesNotSave() {
        when(authorRepository.findByUser(user)).thenReturn(Optional.of(author1));

        authorService.addAuthor(user);
        verify(userService, never()).save(any());
        verify(authorRepository, never()).save(any(Author.class));
        verify(roleService, never()).getRole(anyString());
    }

    @Test
    void testFindAllById() {
        List<Long> ids = Arrays.asList(1L, 2L);
        List<Author> repoResponse = Arrays.asList(author1, author2);
        
        when(authorRepository.findAllById(ids)).thenReturn(repoResponse);

        Set<Author> result = authorService.findAllById(ids);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(author1));
        assertTrue(result.contains(author2));
        
        verify(authorRepository).findAllById(ids);
    }
    
    @Test
    void testFindByUserId() {
        when(authorRepository.findByUser_Id(10L)).thenReturn(author1);
        
        Author result = authorService.findByUserId(10L);
        
        assertNotNull(result);
        assertEquals(author1, result);
        verify(authorRepository).findByUser_Id(10L);
    }
        
    @Test
    void testGetAuthorDetails_Success() {
        author1.setFollowers(new HashSet<>());
        author1.setImage(new Image("url/img.jpg"));
        
        Book book = new Book();
        book.setIsbn("isbn1");
        book.setTitle("Book 1");
        book.setImage(new Image("url/book.jpg"));
        
        Series series = new Series();
        series.setId(100L);
        series.setTitle("Series 1");
        series.setBooks(new HashSet<>());
        series.setFollowers(new HashSet<>());
        
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));
        when(bookRepository.findByAuthors_Id(1L)).thenReturn(Collections.singletonList(book));
        when(seriesRepository.findByAuthorId(1L)).thenReturn(Collections.singletonList(series));
        
        AuthorDetailResponse response = authorService.getAuthorDetails(1L);
        
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("James", response.getFirstName());
        assertEquals("url/img.jpg", response.getImageUrl());
        assertEquals(1, response.getBookCount());
        assertEquals(1, response.getSeriesCount());
        
        assertEquals(1, response.getBooks().size());
        assertEquals("Book 1", response.getBooks().get(0).getTitle());
        
        assertEquals(1, response.getSeries().size());
        assertEquals("Series 1", response.getSeries().get(0).getTitle());
    }
    
    @Test
    void testGetAuthorDetails_NotFound() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class, () -> authorService.getAuthorDetails(99L));
    }
}