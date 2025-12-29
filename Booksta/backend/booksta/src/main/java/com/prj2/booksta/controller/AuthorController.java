package com.prj2.booksta.controller;

import com.prj2.booksta.model.Author;
import com.prj2.booksta.model.Image;
import com.prj2.booksta.model.dto.AuthorDetailResponse;
import com.prj2.booksta.model.dto.UpdateAuthor;
import com.prj2.booksta.service.AuthorService;
import com.prj2.booksta.service.FileStorageService;
import com.prj2.booksta.service.ImageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
@CrossOrigin(origins = "*")
public class AuthorController {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ImageService imageService;

    @GetMapping
    public List<Author> getAuthors() {
        return (List<Author>) authorService.getAllAuthors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDetailResponse> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorDetails(id));
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Author addAuthor(
            @RequestPart("author") Author author,
            @RequestPart(name = "image", required = false) MultipartFile file,
            @RequestPart(name = "imageUrl", required = false) String imageUrl
    ) throws IOException {
        Author savedAuthor = authorService.save(author);
        Image picture = null;

        if (file != null && !file.isEmpty()) {
            picture = new Image();
            picture.setUrl(fileStorageService.saveAuthorImage(file, savedAuthor.getId()));
        } else if (imageUrl != null && !imageUrl.isBlank()) {
            picture = new Image();
            picture.setUrl(imageUrl);
        }

        if (picture != null) {
            Image img = imageService.createImage(picture);
            savedAuthor.setImage(img);
        }

        return authorService.save(savedAuthor);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<Author> updateAuthor(
            @PathVariable Long id,
            @RequestPart("author") UpdateAuthor authorDto,
            @RequestPart(name = "image", required = false) MultipartFile file
    ) throws IOException {
        Author author = authorService.getAuthorById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));

        if (authorDto.firstName() != null && !authorDto.firstName().isBlank()) {
            author.setFirstName(authorDto.firstName());
        }
        if (authorDto.lastName() != null && !authorDto.lastName().isBlank()) {
            author.setLastName(authorDto.lastName());
        }

        if (file != null && !file.isEmpty()) {
            Image image = author.getImage();
            if (image == null) {
                image = new Image();
            }
            image.setUrl(fileStorageService.saveAuthorImage(file, id));
            imageService.createImage(image);
            author.setImage(image);
        } else if (authorDto.imageUrl() != null && !authorDto.imageUrl().isBlank()) {
            Image image = author.getImage();
            if (image == null) {
                image = new Image();
            }
            image.setUrl(authorDto.imageUrl());
            imageService.createImage(image);
            author.setImage(image);
        }

        return ResponseEntity.ok(authorService.save(author));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
