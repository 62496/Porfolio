package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj2.booksta.model.Author;
import com.prj2.booksta.model.Image;
import com.prj2.booksta.model.dto.AuthorDetailResponse;
import com.prj2.booksta.model.dto.UpdateAuthor;
import com.prj2.booksta.service.AuthorService;
import com.prj2.booksta.service.FileStorageService;
import com.prj2.booksta.service.ImageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    @Mock
    private AuthorService authorService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private AuthorController authorController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Author author;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();

        author = new Author();
        author.setId(1L);
        author.setFirstName("Victor");
        author.setLastName("Hugo");
    }

    @Test
    void testGetAuthors() throws Exception {
        when(authorService.getAllAuthors()).thenReturn(Collections.singletonList(author));

        mockMvc.perform(get("/api/authors")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("Victor"));

        verify(authorService).getAllAuthors();
    }

    @Test
    void testGetAuthorById() throws Exception {
        AuthorDetailResponse response = new AuthorDetailResponse();
        response.setId(1L);
        response.setFirstName("Victor");
        response.setLastName("Hugo");

        when(authorService.getAuthorDetails(1L)).thenReturn(response);

        mockMvc.perform(get("/api/authors/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Victor"));

        verify(authorService).getAuthorDetails(1L);
    }

    @Test
    void testAddAuthor_WithFile() throws Exception {
        MockMultipartFile authorPart = new MockMultipartFile(
                "author", "", "application/json",
                objectMapper.writeValueAsString(author).getBytes(StandardCharsets.UTF_8));

        MockMultipartFile filePart = new MockMultipartFile(
                "image", "photo.jpg", "image/jpeg", "content".getBytes());

        when(authorService.save(any(Author.class))).thenReturn(author);
        when(fileStorageService.saveAuthorImage(any(), eq(1L))).thenReturn("path/to/photo.jpg");

        Image savedImage = new Image();
        savedImage.setUrl("path/to/photo.jpg");
        when(imageService.createImage(any(Image.class))).thenReturn(savedImage);

        mockMvc.perform(multipart("/api/authors")
                .file(authorPart)
                .file(filePart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(fileStorageService).saveAuthorImage(any(), eq(1L));
        verify(imageService).createImage(any(Image.class));
        verify(authorService, times(2)).save(any(Author.class));
    }

    @Test
    void testAddAuthor_WithImageUrl() throws Exception {
        MockMultipartFile authorPart = new MockMultipartFile(
                "author", "", "application/json",
                objectMapper.writeValueAsString(author).getBytes(StandardCharsets.UTF_8));

        MockMultipartFile urlPart = new MockMultipartFile(
                "imageUrl", "", "text/plain", "http://example.com/avatar.png".getBytes());

        when(authorService.save(any(Author.class))).thenReturn(author);

        Image savedImage = new Image();
        savedImage.setUrl("http://example.com/avatar.png");
        when(imageService.createImage(any(Image.class))).thenReturn(savedImage);

        mockMvc.perform(multipart("/api/authors")
                .file(authorPart)
                .file(urlPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(fileStorageService, never()).saveAuthorImage(any(), any());
        verify(imageService).createImage(any(Image.class));
        verify(authorService, times(2)).save(any(Author.class));
    }

    @Test
    void testAddAuthor_NoImage() throws Exception {
        MockMultipartFile authorPart = new MockMultipartFile(
                "author", "", "application/json",
                objectMapper.writeValueAsString(author).getBytes(StandardCharsets.UTF_8));

        when(authorService.save(any(Author.class))).thenReturn(author);

        mockMvc.perform(multipart("/api/authors")
                .file(authorPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(fileStorageService, never()).saveAuthorImage(any(), any());
        verify(imageService, never()).createImage(any(Image.class));
        verify(authorService, times(2)).save(any(Author.class));
    }

    @Test
    void testUpdateAuthor_WithFile_NoExistingImage() throws Exception {
        UpdateAuthor updateDto = new UpdateAuthor("VictorUpdated", "HugoUpdated", null);
        
        MockMultipartFile authorPart = new MockMultipartFile(
                "author", "", "application/json",
                objectMapper.writeValueAsString(updateDto).getBytes(StandardCharsets.UTF_8));

        MockMultipartFile filePart = new MockMultipartFile(
                "image", "new.jpg", "image/jpeg", "content".getBytes());

        when(authorService.getAuthorById(1L)).thenReturn(Optional.of(author));
        when(fileStorageService.saveAuthorImage(any(), eq(1L))).thenReturn("new/path.jpg");
        when(imageService.createImage(any(Image.class))).thenReturn(new Image("new/path.jpg"));
        when(authorService.save(any(Author.class))).thenReturn(author);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/authors/{id}", 1L)
                .file(authorPart)
                .file(filePart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        assertEquals("VictorUpdated", author.getFirstName());
        assertEquals("HugoUpdated", author.getLastName());
        verify(imageService).createImage(any(Image.class));
    }

    @Test
    void testUpdateAuthor_WithFile_ExistingImage() throws Exception {
        Image existingImage = new Image("old.jpg");
        author.setImage(existingImage);

        UpdateAuthor updateDto = new UpdateAuthor(null, null, null);
        
        MockMultipartFile authorPart = new MockMultipartFile(
                "author", "", "application/json",
                objectMapper.writeValueAsString(updateDto).getBytes(StandardCharsets.UTF_8));

        MockMultipartFile filePart = new MockMultipartFile(
                "image", "new.jpg", "image/jpeg", "content".getBytes());

        when(authorService.getAuthorById(1L)).thenReturn(Optional.of(author));
        when(fileStorageService.saveAuthorImage(any(), eq(1L))).thenReturn("updated/path.jpg");
        when(authorService.save(any(Author.class))).thenReturn(author);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/authors/{id}", 1L)
                .file(authorPart)
                .file(filePart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        assertEquals("updated/path.jpg", author.getImage().getUrl());
        verify(imageService).createImage(existingImage);
    }

    @Test
    void testUpdateAuthor_WithDtoUrl_NoExistingImage() throws Exception {
        UpdateAuthor updateDto = new UpdateAuthor("Victor", "Hugo", "http://new-url.com");
        
        MockMultipartFile authorPart = new MockMultipartFile(
                "author", "", "application/json",
                objectMapper.writeValueAsString(updateDto).getBytes(StandardCharsets.UTF_8));

        when(authorService.getAuthorById(1L)).thenReturn(Optional.of(author));
        when(imageService.createImage(any(Image.class))).thenReturn(new Image("http://new-url.com"));
        when(authorService.save(any(Author.class))).thenReturn(author);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/authors/{id}", 1L)
                .file(authorPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        assertEquals("http://new-url.com", author.getImage().getUrl());
    }

    @Test
    void testUpdateAuthor_WithDtoUrl_ExistingImage() throws Exception {
        Image existingImage = new Image("old.jpg");
        author.setImage(existingImage);

        UpdateAuthor updateDto = new UpdateAuthor(null, null, "http://update-url.com");
        
        MockMultipartFile authorPart = new MockMultipartFile(
                "author", "", "application/json",
                objectMapper.writeValueAsString(updateDto).getBytes(StandardCharsets.UTF_8));

        when(authorService.getAuthorById(1L)).thenReturn(Optional.of(author));
        when(authorService.save(any(Author.class))).thenReturn(author);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/authors/{id}", 1L)
                .file(authorPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        assertEquals("http://update-url.com", author.getImage().getUrl());
        verify(imageService).createImage(existingImage);
    }

}