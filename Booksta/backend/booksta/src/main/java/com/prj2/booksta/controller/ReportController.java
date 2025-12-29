package com.prj2.booksta.controller;

import com.prj2.booksta.model.Report;
import com.prj2.booksta.model.dto.ResolveBookReport;
import com.prj2.booksta.service.BookService;
import com.prj2.booksta.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @PostMapping(
            value = "/books/{reportId}/resolve",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Report> resolveBookReport(
            @PathVariable long reportId,
            @RequestPart("data") ResolveBookReport body,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return ResponseEntity.ok(reportService.resolveBookReport(reportId, body, image));
    }

    @PostMapping(
            value = "/books/{reportId}/dismiss"
    )
    public ResponseEntity<Report> dismissBookReport(@PathVariable long reportId) {
        return ResponseEntity.ok(reportService.dismissBookReport(reportId));
    }
}
