package com.prj2.booksta.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {

    @InjectMocks
    private FileStorageService fileStorageService;

    private final Path booksDir = Paths.get("uploads/books").toAbsolutePath().normalize();
    private final Path authorsDir = Paths.get("uploads/authors").toAbsolutePath().normalize();
    private final Path collectionsDir = Paths.get("uploads/collections").toAbsolutePath().normalize();

    private final String ISBN = "testBook";
    private final Long AUTHOR_ID = 1L;
    private final Long COLLECTION_ID = 50L;
    private final String MOCK_BACKEND_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() throws IOException {
        ReflectionTestUtils.setField(fileStorageService, "backendUrl", MOCK_BACKEND_URL);
        
        Files.createDirectories(booksDir);
        Files.createDirectories(authorsDir);
        Files.createDirectories(collectionsDir);
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(booksDir.resolve(ISBN + ".png"));
        Files.deleteIfExists(authorsDir.resolve(AUTHOR_ID + ".png"));
        Files.deleteIfExists(collectionsDir.resolve(COLLECTION_ID + ".png"));
    }

    @Test
    void testSaveBookImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cover.png",
                "image/png",
                "Book content".getBytes());

        String resultUrl = fileStorageService.saveBookImage(file, ISBN);

        assertEquals(MOCK_BACKEND_URL + "/api/images/books/" + ISBN, resultUrl);
        assertTrue(Files.exists(booksDir.resolve(ISBN + ".png")));
    }

    @Test
    void testSaveAuthorImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "author.png",
                "image/png",
                "Author content".getBytes());

        String resultUrl = fileStorageService.saveAuthorImage(file, AUTHOR_ID);

        assertEquals(MOCK_BACKEND_URL + "/api/images/authors/" + AUTHOR_ID, resultUrl);
        assertTrue(Files.exists(authorsDir.resolve(AUTHOR_ID + ".png")));
    }

    @Test
    void testSaveCollectionImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "collection.png",
                "image/png",
                "Collection content".getBytes());

        String resultUrl = fileStorageService.saveCollectionImage(file, COLLECTION_ID);

        assertEquals(MOCK_BACKEND_URL + "/api/images/collections/" + COLLECTION_ID, resultUrl);
        assertTrue(Files.exists(collectionsDir.resolve(COLLECTION_ID + ".png")));
    }

    @Test
    void testDeleteCollectionImage() throws IOException {
        Path targetFile = collectionsDir.resolve(COLLECTION_ID + ".png");
        Files.createFile(targetFile);
        assertTrue(Files.exists(targetFile));

        fileStorageService.deleteCollectionImage(COLLECTION_ID);

        assertFalse(Files.exists(targetFile));
    }

    @Test
    void testDeleteCollectionImage_FileDoesNotExist() {
        assertDoesNotThrow(() -> fileStorageService.deleteCollectionImage(999L));
    }
}