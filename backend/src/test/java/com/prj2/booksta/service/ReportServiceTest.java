package com.prj2.booksta.service;

import com.prj2.booksta.model.*;
import com.prj2.booksta.model.dto.ResolveBookReport;
import com.prj2.booksta.repository.BookReportRepository;
import com.prj2.booksta.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock private BookReportRepository bookReportRepository;
    @Mock private ReportRepository reportRepository;
    @Mock private BookService bookService;
    @Mock private FileStorageService fileStorageService;
    @Mock private ImageService imageService;
    @Mock private AuthorService authorService;
    @Mock private SubjectService subjectService;

    @InjectMocks
    private ReportService reportService;

    private Report report;
    private Book book;

    @BeforeEach
    void setUp() {
        report = new Report();
        report.setId(1L);
        report.setReportStatus(ReportStatus.PENDING);

        book = new Book();
        book.setIsbn("12345");
        book.setTitle("Old Title");
    }

    // --- Tests Simples (Create/Get) ---

    @Test
    void testCreateBookReport() {
        BookReport bookReport = new BookReport();
        when(bookReportRepository.save(bookReport)).thenReturn(bookReport);

        BookReport result = reportService.createBookReport(bookReport);

        assertNotNull(result);
        verify(bookReportRepository).save(bookReport);
    }

    @Test
    void testGetAllReports() {
        when(reportRepository.findAll()).thenReturn(Collections.singletonList(report));

        List<Report> results = reportService.getAllReports();

        assertFalse(results.isEmpty());
        verify(reportRepository).findAll();
    }

    // --- Tests de Dismiss (Rejeter le signalement) ---

    @Test
    void testDismissBookReport_Success() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(reportRepository.save(report)).thenReturn(report);

        Report result = reportService.dismissBookReport(1L);

        assertEquals(ReportStatus.DISMISSED, result.getReportStatus());
        verify(reportRepository).save(report);
    }

    @Test
    void testDismissBookReport_AlreadyHandled() {
        report.setReportStatus(ReportStatus.RESOLVED);
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

        reportService.dismissBookReport(1L);

        verify(reportRepository, never()).save(any());
    }

    @Test
    void testDismissBookReport_NotFound() {
        when(reportRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reportService.dismissBookReport(1L));
    }

    // --- Tests de Resolve (Résoudre le signalement) ---

    @Test
    void testResolveBookReport_WarnAuthor() throws IOException {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(reportRepository.save(report)).thenReturn(report);

        // CORRECTION DTO : Respect strict de l'ordre et des types
        ResolveBookReport dto = new ResolveBookReport(
                "WARN_AUTHOR", // action
                null,          // isbn
                null,          // publishingYear
                null,          // pages
                null,          // bookTitle
                null,          // description
                null,          // authors
                null           // subjects
        );

        reportService.resolveBookReport(1L, dto, null);

        assertEquals(ReportStatus.RESOLVED, report.getReportStatus());
        verify(bookService, never()).save(any());
    }

    @Test
    void testResolveBookReport_EditBook_NoImage() throws IOException {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(reportRepository.save(report)).thenReturn(report);
        when(bookService.getBookByIsbn("12345")).thenReturn(book);

        // CORRECTION DTO : Ajout de 'L' pour le Long (500L)
        ResolveBookReport dto = new ResolveBookReport(
                "EDIT_BOOK",   // action
                "12345",       // isbn
                2023,          // publishingYear (Integer)
                500L,          // pages (Long) -> C'était l'erreur ici !
                "New Title",   // bookTitle
                "New Desc",    // description
                null,          // authors
                null           // subjects
        );

        reportService.resolveBookReport(1L, dto, null);

        // Vérifications
        assertEquals("New Title", book.getTitle()); 
        assertEquals(500L, book.getPages());         
        assertEquals("New Desc", book.getDescription());
        assertEquals(2023, book.getPublishingYear());
        assertEquals(ReportStatus.RESOLVED, report.getReportStatus());
        
        verify(bookService).save(book);
    }

    @Test
    void testResolveBookReport_EditBook_WithImage() throws IOException {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(reportRepository.save(report)).thenReturn(report);
        when(bookService.getBookByIsbn("12345")).thenReturn(book);

        // Mock image
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
        when(fileStorageService.saveBookImage(file, "12345")).thenReturn("path/to/img.jpg");
        
        Image savedImage = new Image(); savedImage.setId(10L); savedImage.setUrl("path/to/img.jpg");
        when(imageService.createImage(any(Image.class))).thenReturn(savedImage);

        // DTO correct
        ResolveBookReport dto = new ResolveBookReport(
                "EDIT_BOOK", "12345", null, null, null, null, null, null
        );

        reportService.resolveBookReport(1L, dto, file);

        assertNotNull(book.getImage());
        assertEquals("path/to/img.jpg", book.getImage().getUrl());
        verify(fileStorageService).saveBookImage(file, "12345");
        verify(imageService).createImage(any(Image.class));
        verify(bookService).save(book);
    }

    @Test
    void testResolveBookReport_AlreadyResolved() throws IOException {
        report.setReportStatus(ReportStatus.RESOLVED);
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

        ResolveBookReport dto = new ResolveBookReport("EDIT_BOOK", null, null, null, null, null, null, null);

        reportService.resolveBookReport(1L, dto, null);

        verify(bookService, never()).getBookByIsbn(any());
        verify(reportRepository, never()).save(any());
    }
}