package com.prj2.booksta.controller;

import com.prj2.booksta.model.dto.BookSummary;
import com.prj2.booksta.model.dto.SeriesRequest;
import com.prj2.booksta.model.dto.SeriesResponse;
import com.prj2.booksta.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/series")
@CrossOrigin(origins = "*")
public class SeriesController {

    @Autowired
    private SeriesService seriesService;

    @GetMapping
    public ResponseEntity<List<SeriesResponse>> getAllSeries() {
        return ResponseEntity.ok(seriesService.getAllSeries());
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<SeriesResponse>> getSeriesByAuthorId(@PathVariable Long authorId) {
        return ResponseEntity.ok(seriesService.getSeriesByAuthorId(authorId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeriesResponse> getSeriesById(@PathVariable Long id) {
        return ResponseEntity.ok(seriesService.getSeriesById(id));
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<List<BookSummary>> getSeriesBooks(@PathVariable Long id) {
        return ResponseEntity.ok(seriesService.getSeriesBooks(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('AUTHOR')")
    public ResponseEntity<SeriesResponse> createSeries(@RequestBody SeriesRequest request) {
        SeriesResponse saved = seriesService.createSeries(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AUTHOR') and @seriesAccessChecker.isAuthorOfSeries(authentication, #id)")
    public ResponseEntity<SeriesResponse> updateSeries(
            @PathVariable Long id,
            @RequestBody SeriesRequest request) {
        return ResponseEntity.ok(seriesService.updateSeries(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AUTHOR') and @seriesAccessChecker.isAuthorOfSeries(authentication, #id)")
    public ResponseEntity<Void> deleteSeries(@PathVariable Long id) {
        seriesService.deleteSeries(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/books/{isbn}")
    @PreAuthorize("hasRole('AUTHOR') and @seriesAccessChecker.isAuthorOfSeries(authentication, #id) and @authorAccessChecker.isAuthorOfBook(authentication, #isbn)")
    public ResponseEntity<SeriesResponse> addBookToSeries(
            @PathVariable Long id,
            @PathVariable String isbn) {
        return ResponseEntity.ok(seriesService.addBookToSeries(id, isbn));
    }

    @DeleteMapping("/{id}/books/{isbn}")
    @PreAuthorize("hasRole('AUTHOR') and @seriesAccessChecker.isAuthorOfSeries(authentication, #id) and @authorAccessChecker.isAuthorOfBook(authentication, #isbn)")
    public ResponseEntity<SeriesResponse> removeBookFromSeries(
            @PathVariable Long id,
            @PathVariable String isbn) {
        return ResponseEntity.ok(seriesService.removeBookFromSeries(id, isbn));
    }
}
