package com.prj2.booksta.service;

import com.prj2.booksta.model.*;
import com.prj2.booksta.model.dto.ResolveBookReport;
import com.prj2.booksta.repository.AuthorReportRepository;
import com.prj2.booksta.repository.BookReportRepository;
import com.prj2.booksta.repository.ReportRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private BookReportRepository bookReportRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private SubjectService subjectService;

    public BookReport createBookReport(BookReport bookReport) {
        return bookReportRepository.save(bookReport);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Report save(Report report) {
        return reportRepository.save(report);
    }

    @Transactional
    public Report resolveBookReport(Long reportId, ResolveBookReport bookReport, MultipartFile image) throws IOException {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new RuntimeException("Report not found"));

        if (report.getReportStatus() == ReportStatus.RESOLVED || report.getReportStatus() == ReportStatus.DISMISSED) {
            return report;
        }

        switch (bookReport.action()) {
            case "WARN_AUTHOR" -> {
                // send warning, no image needed
            }
            case "EDIT_BOOK" -> {
                String isbn = bookReport.isbn();
                Book book = bookService.getBookByIsbn(isbn);
                if (image != null) {
                    Image img = new Image();
                    img.setUrl(fileStorageService.saveBookImage(image, isbn));
                    img = imageService.createImage(img);
                    book.setImage(img);
                }
                if (bookReport.publishingYear() != null)
                    book.setPublishingYear(bookReport.publishingYear());

                if (bookReport.pages() != null)
                    book.setPages(bookReport.pages());

                if (bookReport.bookTitle() != null)
                    book.setTitle(bookReport.bookTitle());

                if (bookReport.description() != null)
                    book.setDescription(bookReport.description());

                if (bookReport.authors() != null)
                    book.setAuthors(authorService.findAllById(bookReport.authors()));

                if (bookReport.subjects() != null)
                    book.setSubjects(subjectService.findAllById(bookReport.subjects()));
                bookService.save(book);
            }
        }

        report.setReportStatus(ReportStatus.RESOLVED);
        return save(report);
    }

    public Report dismissBookReport(long reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new RuntimeException("Report not found"));

        if (report.getReportStatus() == ReportStatus.DISMISSED || report.getReportStatus() == ReportStatus.RESOLVED) {
            return report;
        }

        report.setReportStatus(ReportStatus.DISMISSED);
        return reportRepository.save(report);
    }
}
