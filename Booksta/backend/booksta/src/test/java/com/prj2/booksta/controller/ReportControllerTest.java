package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj2.booksta.model.Report;
import com.prj2.booksta.model.dto.ResolveBookReport;
import com.prj2.booksta.service.BookService;
import com.prj2.booksta.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private ReportController reportController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();
    }

    @Test
    void testGetAllReports() throws Exception {
        when(reportService.getAllReports()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/reports")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(reportService).getAllReports();
    }

    @Test
    void testResolveBookReport_WithImage() throws Exception {
        long reportId = 1L;
        Report resolvedReport = new Report();
        resolvedReport.setId(reportId);

        ResolveBookReport dto = new ResolveBookReport(
                "EDIT_BOOK", "123", 2020, 100L, "Title", "Desc", null, null);

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                objectMapper.writeValueAsString(dto).getBytes(StandardCharsets.UTF_8));

        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "cover.jpg",
                "image/jpeg",
                "fake-image".getBytes());

        when(reportService.resolveBookReport(eq(reportId), any(ResolveBookReport.class), any()))
                .thenReturn(resolvedReport);

        mockMvc.perform(multipart("/api/reports/books/{reportId}/resolve", reportId)
                .file(dataPart)
                .file(imagePart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(reportService).resolveBookReport(eq(reportId), any(ResolveBookReport.class), any());
    }

    @Test
    void testResolveBookReport_WithoutImage() throws Exception {
        long reportId = 1L;
        ResolveBookReport dto = new ResolveBookReport(
                "WARN_AUTHOR", null, null, null, null, null, null, null);

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                objectMapper.writeValueAsString(dto).getBytes(StandardCharsets.UTF_8));

        when(reportService.resolveBookReport(eq(reportId), any(ResolveBookReport.class), eq(null)))
                .thenReturn(new Report());

        mockMvc.perform(multipart("/api/reports/books/{reportId}/resolve", reportId)
                .file(dataPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(reportService).resolveBookReport(eq(reportId), any(ResolveBookReport.class), eq(null));
    }

    @Test
    void testDismissBookReport() throws Exception {
        long reportId = 5L;
        when(reportService.dismissBookReport(reportId)).thenReturn(new Report());

        mockMvc.perform(post("/api/reports/books/{reportId}/dismiss", reportId))
                .andExpect(status().isOk());

        verify(reportService).dismissBookReport(reportId);
    }
}