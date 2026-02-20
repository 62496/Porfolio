package com.prj2.booksta.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/images")
public class FileStorageController {

    @GetMapping("/authors/{id}")
    public ResponseEntity<Resource> getAuthorImage(@PathVariable Long id) throws IOException {
        Path filePath = Paths.get("uploads/authors").resolve(id + ".png").normalize();
        Resource resource = new FileSystemResource(filePath.toFile());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }

    @GetMapping("/books/{isbn}")
    public ResponseEntity<Resource> getBookImage(@PathVariable String isbn) throws IOException {
        Path filePath = Paths.get("uploads/books").resolve(isbn + ".png").normalize();
        Resource resource = new FileSystemResource(filePath.toFile());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }

    @GetMapping("/collections/{id}")
    public ResponseEntity<Resource> getCollectionImage(@PathVariable Long id) throws IOException {
        Path filePath = Paths.get("uploads/collections").resolve(id + ".png").normalize();
        Resource resource = new FileSystemResource(filePath.toFile());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }
}
