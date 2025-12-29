package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj2.booksta.exception.GlobalExceptionHandler;
import com.prj2.booksta.model.Author;
import com.prj2.booksta.model.Image;
import com.prj2.booksta.model.dto.AuthorDetailResponse;
import com.prj2.booksta.service.AuthorService;
import com.prj2.booksta.service.FileStorageService;
import com.prj2.booksta.service.ImageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthorService authorService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private AuthorController authorController;

    private Author testAuthor;
    private AuthorDetailResponse testAuthorDetailResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authorController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        testAuthor = new Author();
        testAuthor.setId(1L);
        testAuthor.setFirstName("John");
        testAuthor.setLastName("Doe");
        testAuthor.setBooks(new HashSet<>());
        testAuthor.setFollowers(new HashSet<>());

        testAuthorDetailResponse = new AuthorDetailResponse(
                1L,
                "John",
                "Doe",
                "http://example.com/author.jpg",
                100,  // followerCount
                5,    // bookCount
                2,    // seriesCount
                Collections.emptyList(),  // books
                Collections.emptyList()   // series
        );
    }

    @Nested
    @DisplayName("GET /api/authors tests")
    class GetAllAuthorsTests {

        @Test
        @DisplayName("Should return empty list when no authors exist")
        void getAuthors_NoAuthorsExist_ReturnsEmptyList() throws Exception {
            when(authorService.getAllAuthors()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/authors"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());

            verify(authorService, times(1)).getAllAuthors();
        }

        @Test
        @DisplayName("Should return all authors when authors exist")
        void getAuthors_AuthorsExist_ReturnsAllAuthors() throws Exception {
            Author author2 = new Author();
            author2.setId(2L);
            author2.setFirstName("Jane");
            author2.setLastName("Smith");
            author2.setBooks(new HashSet<>());
            author2.setFollowers(new HashSet<>());

            when(authorService.getAllAuthors()).thenReturn(Arrays.asList(testAuthor, author2));

            mockMvc.perform(get("/api/authors"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].firstName").value("John"))
                    .andExpect(jsonPath("$[1].firstName").value("Jane"));

            verify(authorService, times(1)).getAllAuthors();
        }
    }

    @Nested
    @DisplayName("GET /api/authors/{id} tests")
    class GetAuthorByIdTests {

        @Test
        @DisplayName("Should return author details when ID exists")
        void getAuthorById_AuthorExists_ReturnsAuthorDetails() throws Exception {
            when(authorService.getAuthorDetails(1L)).thenReturn(testAuthorDetailResponse);

            mockMvc.perform(get("/api/authors/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.followerCount").value(100))
                    .andExpect(jsonPath("$.bookCount").value(5))
                    .andExpect(jsonPath("$.seriesCount").value(2));

            verify(authorService).getAuthorDetails(1L);
        }

        @Test
        @DisplayName("Should return 404 when author not found")
        void getAuthorById_AuthorNotFound_Returns404() throws Exception {
            when(authorService.getAuthorDetails(999L))
                    .thenThrow(new EntityNotFoundException("Author not found"));

            mockMvc.perform(get("/api/authors/999"))
                    .andExpect(status().isNotFound());

            verify(authorService).getAuthorDetails(999L);
        }
    }

    @Nested
    @DisplayName("POST /api/authors tests")
    class AddAuthorTests {

        @Test
        @DisplayName("Should create author with image file")
        void addAuthor_WithImageFile_ReturnsCreatedAuthor() throws Exception {
            Author authorToSave = new Author();
            authorToSave.setFirstName("John");
            authorToSave.setLastName("Doe");

            Author savedAuthor = new Author();
            savedAuthor.setId(1L);
            savedAuthor.setFirstName("John");
            savedAuthor.setLastName("Doe");

            MockMultipartFile imageFile = new MockMultipartFile(
                    "image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes()
            );

            MockMultipartFile authorPart = new MockMultipartFile(
                    "author", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(authorToSave)
            );

            when(authorService.save(any(Author.class))).thenReturn(savedAuthor);
            when(fileStorageService.saveAuthorImage(any(), eq(1L))).thenReturn("http://example.com/author.jpg");
            when(imageService.createImage(any(Image.class))).thenAnswer(i -> i.getArgument(0));

            mockMvc.perform(multipart("/api/authors")
                            .file(authorPart)
                            .file(imageFile))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.firstName").value("John"));

            verify(authorService, times(2)).save(any(Author.class));
            verify(fileStorageService).saveAuthorImage(any(), eq(1L));
        }

        @Test
        @DisplayName("Should create author with image URL")
        void addAuthor_WithImageUrl_ReturnsCreatedAuthor() throws Exception {
            Author authorToSave = new Author();
            authorToSave.setFirstName("John");
            authorToSave.setLastName("Doe");

            Author savedAuthor = new Author();
            savedAuthor.setId(1L);
            savedAuthor.setFirstName("John");
            savedAuthor.setLastName("Doe");

            MockMultipartFile authorPart = new MockMultipartFile(
                    "author", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(authorToSave)
            );

            MockMultipartFile imageUrlPart = new MockMultipartFile(
                    "imageUrl", "", MediaType.TEXT_PLAIN_VALUE,
                    "http://example.com/existing-image.jpg".getBytes()
            );

            when(authorService.save(any(Author.class))).thenReturn(savedAuthor);
            when(imageService.createImage(any(Image.class))).thenAnswer(i -> i.getArgument(0));

            mockMvc.perform(multipart("/api/authors")
                            .file(authorPart)
                            .file(imageUrlPart))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));

            verify(authorService, times(2)).save(any(Author.class));
            verify(fileStorageService, never()).saveAuthorImage(any(), anyLong());
        }

        @Test
        @DisplayName("Should create author without image")
        void addAuthor_WithoutImage_ReturnsCreatedAuthor() throws Exception {
            Author authorToSave = new Author();
            authorToSave.setFirstName("John");
            authorToSave.setLastName("Doe");

            Author savedAuthor = new Author();
            savedAuthor.setId(1L);
            savedAuthor.setFirstName("John");
            savedAuthor.setLastName("Doe");

            MockMultipartFile authorPart = new MockMultipartFile(
                    "author", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(authorToSave)
            );

            when(authorService.save(any(Author.class))).thenReturn(savedAuthor);

            mockMvc.perform(multipart("/api/authors")
                            .file(authorPart))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));

            verify(authorService, times(2)).save(any(Author.class));
            verify(imageService, never()).createImage(any());
        }
    }

    @Nested
    @DisplayName("PUT /api/authors/{id} tests")
    class UpdateAuthorTests {

        @Test
        @DisplayName("Should update author successfully")
        void updateAuthor_ValidRequest_ReturnsUpdatedAuthor() throws Exception {
            Author existingAuthor = new Author();
            existingAuthor.setId(1L);
            existingAuthor.setFirstName("John");
            existingAuthor.setLastName("Doe");

            Author updatedAuthor = new Author();
            updatedAuthor.setId(1L);
            updatedAuthor.setFirstName("Johnny");
            updatedAuthor.setLastName("Doe");

            String updateJson = "{\"firstName\":\"Johnny\",\"lastName\":\"Doe\"}";
            MockMultipartFile authorPart = new MockMultipartFile(
                    "author", "", MediaType.APPLICATION_JSON_VALUE, updateJson.getBytes()
            );

            when(authorService.getAuthorById(1L)).thenReturn(Optional.of(existingAuthor));
            when(authorService.save(any(Author.class))).thenReturn(updatedAuthor);

            mockMvc.perform(multipart("/api/authors/1")
                            .file(authorPart)
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            }))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Johnny"));

            verify(authorService).save(any(Author.class));
        }

        @Test
        @DisplayName("Should return 404 when author not found")
        void updateAuthor_AuthorNotFound_Returns404() throws Exception {
            String updateJson = "{\"firstName\":\"Johnny\",\"lastName\":\"Doe\"}";
            MockMultipartFile authorPart = new MockMultipartFile(
                    "author", "", MediaType.APPLICATION_JSON_VALUE, updateJson.getBytes()
            );

            when(authorService.getAuthorById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(multipart("/api/authors/999")
                            .file(authorPart)
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            }))
                    .andExpect(status().isNotFound());

            verify(authorService, never()).save(any());
        }

        @Test
        @DisplayName("Should update author with new image file")
        void updateAuthor_WithNewImage_ReturnsUpdatedAuthor() throws Exception {
            Author existingAuthor = new Author();
            existingAuthor.setId(1L);
            existingAuthor.setFirstName("John");
            existingAuthor.setLastName("Doe");

            String updateJson = "{\"firstName\":\"Johnny\",\"lastName\":\"Doe\"}";
            MockMultipartFile authorPart = new MockMultipartFile(
                    "author", "", MediaType.APPLICATION_JSON_VALUE, updateJson.getBytes()
            );

            MockMultipartFile imageFile = new MockMultipartFile(
                    "image", "new-image.jpg", MediaType.IMAGE_JPEG_VALUE, "new image content".getBytes()
            );

            when(authorService.getAuthorById(1L)).thenReturn(Optional.of(existingAuthor));
            when(fileStorageService.saveAuthorImage(any(), eq(1L))).thenReturn("http://example.com/new-author.jpg");
            when(imageService.createImage(any(Image.class))).thenAnswer(i -> i.getArgument(0));
            when(authorService.save(any(Author.class))).thenReturn(existingAuthor);

            mockMvc.perform(multipart("/api/authors/1")
                            .file(authorPart)
                            .file(imageFile)
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            }))
                    .andExpect(status().isOk());

            verify(fileStorageService).saveAuthorImage(any(), eq(1L));
            verify(imageService).createImage(any(Image.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/authors/{id} tests")
    class DeleteAuthorTests {

        @Test
        @DisplayName("Should delete author successfully")
        void deleteAuthor_AuthorExists_ReturnsNoContent() throws Exception {
            doNothing().when(authorService).deleteAuthor(1L);

            mockMvc.perform(delete("/api/authors/1"))
                    .andExpect(status().isNoContent());

            verify(authorService).deleteAuthor(1L);
        }

        @Test
        @DisplayName("Should return 404 when author not found")
        void deleteAuthor_AuthorNotFound_Returns404() throws Exception {
            doThrow(new EntityNotFoundException("Author not found"))
                    .when(authorService).deleteAuthor(999L);

            mockMvc.perform(delete("/api/authors/999"))
                    .andExpect(status().isNotFound());

            verify(authorService).deleteAuthor(999L);
        }
    }
}
