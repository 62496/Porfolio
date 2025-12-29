package com.prj2.booksta.controller;

import com.prj2.booksta.model.BookCollection;
import com.prj2.booksta.service.BookCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
@CrossOrigin(origins = "*")
public class BookCollectionController {

    @Autowired
    private BookCollectionService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookCollection> create(
            @RequestPart("collection") BookCollection collection,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createCollection(collection, image));
    }

    @PutMapping(value = "/{collectionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookCollection> update(
            @PathVariable Long collectionId,
            @RequestPart("collection") BookCollection collection,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(service.updateCollection(collectionId, collection, image));
    }

    @DeleteMapping("/{collectionId}")
    public ResponseEntity<Void> delete(@PathVariable Long collectionId) {
        service.deleteCollection(collectionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<BookCollection>> getOwnCollections() {
        return ResponseEntity.ok(service.getAllOwnCollections());
    }

    @GetMapping("/allowed")
    public ResponseEntity<List<BookCollection>> getAllowedCollections() {
        return ResponseEntity.ok(service.getAllCollectionsAllowed());
    }

    @GetMapping("/public")
    public ResponseEntity<List<BookCollection>> getPublicCollections() {
        return ResponseEntity.ok(service.getAllPublicCollections());
    }

    @GetMapping("/shared")
    public ResponseEntity<List<BookCollection>> getSharedWithMe() {
        return ResponseEntity.ok(service.getSharedWithMe());
    }

    @GetMapping("/{collectionId}")
    public ResponseEntity<BookCollection> getCollection(@PathVariable Long collectionId) {
        return service.getCollectionIfAllowed(collectionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @GetMapping("/{collectionId}/access")
    public ResponseEntity<Boolean> canAccess(@PathVariable Long collectionId) {
        return ResponseEntity.ok(service.canAccess(collectionId));
    }

    @PostMapping("/{collectionId}/share/{userEmail}")
    public ResponseEntity<BookCollection> shareWith(
            @PathVariable Long collectionId,
            @PathVariable String userEmail) {
        return ResponseEntity.ok(service.shareWith(collectionId, userEmail));
    }

    @DeleteMapping("/{collectionId}/share/{userId}")
    public ResponseEntity<BookCollection> unshareWith(
            @PathVariable Long collectionId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(service.unshareWith(collectionId, userId));
    }

    @PostMapping("/{collectionId}/books/{isbn}")
    public ResponseEntity<BookCollection> addBook(
            @PathVariable Long collectionId,
            @PathVariable String isbn) {
        return ResponseEntity.ok(service.addBook(collectionId, isbn));
    }

    @DeleteMapping("/{collectionId}/books/{isbn}")
    public ResponseEntity<BookCollection> removeBook(
            @PathVariable Long collectionId,
            @PathVariable String isbn) {
        return ResponseEntity.ok(service.removeBook(collectionId, isbn));
    }

    @GetMapping("/{collectionId}/books/{isbn}")
    public ResponseEntity<Boolean> containsBook(
            @PathVariable Long collectionId,
            @PathVariable String isbn) {
        return ResponseEntity.ok(service.collectionContainsBook(collectionId, isbn));
    }
}
