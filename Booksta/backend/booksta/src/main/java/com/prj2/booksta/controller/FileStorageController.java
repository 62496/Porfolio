package com.prj2.booksta.controller;

import com.prj2.booksta.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/images")
public class FileStorageController {

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/authors/{id}")
    public ResponseEntity<InputStreamResource> getAuthorImage(@PathVariable Long id) throws IOException {
        InputStream stream = fileStorageService.getImage("authors", id.toString());
        int x = 1;
        if (stream == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(new InputStreamResource(stream));
    }

    @GetMapping("/books/{isbn}")
    public ResponseEntity<InputStreamResource> getBookImage(@PathVariable String isbn) throws IOException {
        InputStream stream = fileStorageService.getImage("books", isbn);
        if (stream == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(new InputStreamResource(stream));
    }

    @GetMapping("/collections/{id}")
    public ResponseEntity<InputStreamResource> getCollectionImage(@PathVariable Long id) throws IOException {
        InputStream stream = fileStorageService.getImage("collections", id.toString());
        if (stream == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(new InputStreamResource(stream));
    }
}
