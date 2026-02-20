package com.prj2.booksta.controller;

import com.prj2.booksta.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class FileStorageControllerTest {

    @Mock
    private FileStorageService fileStorageService; // Mocké même si non utilisé, bonne pratique si le code change

    @InjectMocks
    private FileStorageController fileStorageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileStorageController).build();
    }

    // --- TESTS POUR LES LIVRES ---

    @Test
    void testGetBookImage_NotFound() throws Exception {
        String isbn = "999999";
        mockMvc.perform(get("/api/images/books/{isbn}", isbn))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetBookImage_Found() throws Exception {
        String isbn = "test-exist";
        
        // Préparation du dossier et du fichier
        Path uploadDir = Paths.get("uploads/books");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path tempFile = uploadDir.resolve(isbn + ".png");
        byte[] fakeContent = new byte[] { 1, 2, 3, 4 };
        Files.write(tempFile, fakeContent);

        try {
            mockMvc.perform(get("/api/images/books/{isbn}", isbn))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.IMAGE_PNG))
                    .andExpect(content().bytes(fakeContent));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    // --- TESTS POUR LES AUTEURS (AJOUTÉS) ---

    @Test
    void testGetAuthorImage_NotFound() throws Exception {
        Long id = 999999L;
        mockMvc.perform(get("/api/images/authors/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAuthorImage_Found() throws Exception {
        Long id = 12345L;

        // Préparation du dossier et du fichier pour les AUTEURS
        Path uploadDir = Paths.get("uploads/authors");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // Attention: le contrôleur attend "{id}.png"
        Path tempFile = uploadDir.resolve(id + ".png");
        byte[] fakeContent = new byte[] { 5, 6, 7, 8 };
        Files.write(tempFile, fakeContent);

        try {
            mockMvc.perform(get("/api/images/authors/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.IMAGE_PNG))
                    .andExpect(content().bytes(fakeContent));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}