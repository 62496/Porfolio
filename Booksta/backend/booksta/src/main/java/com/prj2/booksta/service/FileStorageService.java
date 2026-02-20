package com.prj2.booksta.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    @Value("${app.backend.url}")
    private String backendUrl;

    private final Path booksDir = Paths.get("uploads/books").toAbsolutePath().normalize();
    private final Path authorsDir = Paths.get("uploads/authors").toAbsolutePath().normalize();
    private final Path collectionsDir = Paths.get("uploads/collections").toAbsolutePath().normalize();

    public FileStorageService() throws IOException {
        Files.createDirectories(booksDir);
        Files.createDirectories(authorsDir);
        Files.createDirectories(collectionsDir);
    }

    public String saveBookImage(MultipartFile file, String isbn) throws IOException {
        saveFile(file, booksDir, isbn + ".png");
        return backendUrl + "/api/images/books/" + isbn;
    }

    public String saveAuthorImage(MultipartFile file, Long id) throws IOException {
        saveFile(file, authorsDir, id + ".png");
        return backendUrl + "/api/images/authors/" + id;
    }

    public String saveCollectionImage(MultipartFile file, Long id) throws IOException {
        saveFile(file, collectionsDir, id + ".png");
        return backendUrl + "/api/images/collections/" + id;
    }

    public void deleteCollectionImage(Long id) throws IOException {
        Path target = collectionsDir.resolve(id + ".png");
        Files.deleteIfExists(target);
    }

    private void saveFile(MultipartFile file, Path dir, String fileName) throws IOException {
        Path target = dir.resolve(fileName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
    }
}
