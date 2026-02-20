package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj2.booksta.model.dto.BookSummary;
import com.prj2.booksta.model.dto.SeriesRequest;
import com.prj2.booksta.model.dto.SeriesResponse;
import com.prj2.booksta.service.SeriesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SeriesControllerTest {

    @Mock
    private SeriesService seriesService;

    @InjectMocks
    private SeriesController seriesController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    private SeriesResponse seriesResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        Object testControllerAdvice = new Object() {
            @ExceptionHandler(RuntimeException.class)
            public ResponseEntity<String> handle(RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        };
        
        mockMvc = MockMvcBuilders.standaloneSetup(seriesController)
                .setControllerAdvice(testControllerAdvice)
                .build();

        seriesResponse = new SeriesResponse();
        seriesResponse.setId(1L);
        seriesResponse.setTitle("Harry Potter");
    }

    @Test
    void testGetAllSeries_Success() throws Exception {
        when(seriesService.getAllSeries()).thenReturn(Arrays.asList(seriesResponse));

        mockMvc.perform(get("/api/series")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Harry Potter"));

        verify(seriesService).getAllSeries();
    }

    @Test
    void testGetAllSeries_Empty() throws Exception {
        when(seriesService.getAllSeries()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/series")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(seriesService).getAllSeries();
    }

    @Test
    void testGetSeriesById_Found() throws Exception {
        when(seriesService.getSeriesById(1L)).thenReturn(seriesResponse);

        mockMvc.perform(get("/api/series/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Harry Potter"));

        verify(seriesService).getSeriesById(1L);
    }

    @Test
    void testCreateSeries_Success() throws Exception {
        SeriesRequest inputRequest = new SeriesRequest();
        inputRequest.setTitle("Lord of the Rings");

        SeriesResponse savedResponse = new SeriesResponse();
        savedResponse.setId(2L);
        savedResponse.setTitle("Lord of the Rings");

        when(seriesService.createSeries(any(SeriesRequest.class))).thenReturn(savedResponse);

        mockMvc.perform(post("/api/series")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.title").value("Lord of the Rings"));

        verify(seriesService).createSeries(any(SeriesRequest.class));
    }

    @Test
    void testGetSeriesByAuthorId() throws Exception {
        Long authorId = 5L;
        when(seriesService.getSeriesByAuthorId(authorId)).thenReturn(Arrays.asList(seriesResponse));

        mockMvc.perform(get("/api/series/author/{authorId}", authorId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Harry Potter"));

        verify(seriesService).getSeriesByAuthorId(authorId);
    }

    @Test
    void testGetSeriesBooks() throws Exception {
        BookSummary bookSummary = new BookSummary(); 
        
        when(seriesService.getSeriesBooks(1L)).thenReturn(Arrays.asList(bookSummary));

        mockMvc.perform(get("/api/series/{id}/books", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(seriesService).getSeriesBooks(1L);
    }

    @Test
    void testUpdateSeries() throws Exception {
        Long seriesId = 1L;
        SeriesRequest updateRequest = new SeriesRequest();
        updateRequest.setTitle("Harry Potter Updated");

        SeriesResponse updatedResponse = new SeriesResponse();
        updatedResponse.setId(seriesId);
        updatedResponse.setTitle("Harry Potter Updated");

        when(seriesService.updateSeries(eq(seriesId), any(SeriesRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/series/{id}", seriesId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Harry Potter Updated"));

        verify(seriesService).updateSeries(eq(seriesId), any(SeriesRequest.class));
    }

    @Test
    void testDeleteSeries() throws Exception {
        Long seriesId = 1L;
        doNothing().when(seriesService).deleteSeries(seriesId);

        mockMvc.perform(delete("/api/series/{id}", seriesId))
                .andExpect(status().isNoContent());

        verify(seriesService).deleteSeries(seriesId);
    }

    @Test
    void testAddBookToSeries() throws Exception {
        Long seriesId = 1L;
        String isbn = "123456789";

        when(seriesService.addBookToSeries(seriesId, isbn)).thenReturn(seriesResponse);

        mockMvc.perform(post("/api/series/{id}/books/{isbn}", seriesId, isbn)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seriesId));

        verify(seriesService).addBookToSeries(seriesId, isbn);
    }

    @Test
    void testRemoveBookFromSeries() throws Exception {
        Long seriesId = 1L;
        String isbn = "123456789";

        when(seriesService.removeBookFromSeries(seriesId, isbn)).thenReturn(seriesResponse);

        mockMvc.perform(delete("/api/series/{id}/books/{isbn}", seriesId, isbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seriesId));

        verify(seriesService).removeBookFromSeries(seriesId, isbn);
    }
}