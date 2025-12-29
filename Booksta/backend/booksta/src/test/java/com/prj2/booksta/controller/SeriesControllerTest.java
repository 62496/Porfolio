package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj2.booksta.exception.GlobalExceptionHandler;
import com.prj2.booksta.model.dto.BookSummary;
import com.prj2.booksta.model.dto.SeriesRequest;
import com.prj2.booksta.model.dto.SeriesResponse;
import com.prj2.booksta.service.SeriesService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SeriesControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private SeriesService seriesService;

    @InjectMocks
    private SeriesController seriesController;

    private SeriesResponse testSeriesResponse;
    private SeriesRequest testSeriesRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(seriesController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        testSeriesResponse = new SeriesResponse(
                1L,
                "Test Series",
                "A test series description",
                new SeriesResponse.AuthorSummary(1L, "John", "Doe", null),
                0,  // bookCount
                0   // followerCount
        );

        testSeriesRequest = new SeriesRequest();
        testSeriesRequest.setTitle("Test Series");
        testSeriesRequest.setDescription("A test series description");
    }

    @Nested
    @DisplayName("GET /api/series tests")
    class GetAllSeriesTests {

        @Test
        @DisplayName("Should return empty list when no series exist")
        void getAllSeries_NoSeriesExist_ReturnsEmptyList() throws Exception {
            when(seriesService.getAllSeries()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/series"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());

            verify(seriesService, times(1)).getAllSeries();
        }

        @Test
        @DisplayName("Should return all series when series exist")
        void getAllSeries_SeriesExist_ReturnsAllSeries() throws Exception {
            SeriesResponse series2 = new SeriesResponse(
                    2L, "Another Series", "Another description",
                    new SeriesResponse.AuthorSummary(1L, "John", "Doe", null), 0, 0
            );

            when(seriesService.getAllSeries()).thenReturn(Arrays.asList(testSeriesResponse, series2));

            mockMvc.perform(get("/api/series"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].title").value("Test Series"))
                    .andExpect(jsonPath("$[1].title").value("Another Series"));

            verify(seriesService, times(1)).getAllSeries();
        }
    }

    @Nested
    @DisplayName("GET /api/series/{id} tests")
    class GetSeriesByIdTests {

        @Test
        @DisplayName("Should return series when ID exists")
        void getSeriesById_SeriesExists_ReturnsSeries() throws Exception {
            when(seriesService.getSeriesById(1L)).thenReturn(testSeriesResponse);

            mockMvc.perform(get("/api/series/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Test Series"))
                    .andExpect(jsonPath("$.description").value("A test series description"));

            verify(seriesService).getSeriesById(1L);
        }

        @Test
        @DisplayName("Should return 404 when series not found")
        void getSeriesById_SeriesNotFound_Returns404() throws Exception {
            when(seriesService.getSeriesById(999L))
                    .thenThrow(new EntityNotFoundException("Series not found"));

            mockMvc.perform(get("/api/series/999"))
                    .andExpect(status().isNotFound());

            verify(seriesService).getSeriesById(999L);
        }
    }

    @Nested
    @DisplayName("GET /api/series/author/{authorId} tests")
    class GetSeriesByAuthorIdTests {

        @Test
        @DisplayName("Should return series for author")
        void getSeriesByAuthorId_SeriesExist_ReturnsSeries() throws Exception {
            when(seriesService.getSeriesByAuthorId(1L)).thenReturn(List.of(testSeriesResponse));

            mockMvc.perform(get("/api/series/author/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].title").value("Test Series"));

            verify(seriesService).getSeriesByAuthorId(1L);
        }

        @Test
        @DisplayName("Should return empty list when author has no series")
        void getSeriesByAuthorId_NoSeries_ReturnsEmptyList() throws Exception {
            when(seriesService.getSeriesByAuthorId(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/series/author/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/series/{id}/books tests")
    class GetSeriesBooksTests {

        @Test
        @DisplayName("Should return books in series")
        void getSeriesBooks_BooksExist_ReturnsBooks() throws Exception {
            BookSummary book = new BookSummary("9781234567890", "Test Book", 2023, null);
            when(seriesService.getSeriesBooks(1L)).thenReturn(List.of(book));

            mockMvc.perform(get("/api/series/1/books"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].isbn").value("9781234567890"))
                    .andExpect(jsonPath("$[0].title").value("Test Book"));

            verify(seriesService).getSeriesBooks(1L);
        }

        @Test
        @DisplayName("Should return empty list when no books in series")
        void getSeriesBooks_NoBooksInSeries_ReturnsEmptyList() throws Exception {
            when(seriesService.getSeriesBooks(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/series/1/books"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("Should return 404 when series not found")
        void getSeriesBooks_SeriesNotFound_Returns404() throws Exception {
            when(seriesService.getSeriesBooks(999L))
                    .thenThrow(new EntityNotFoundException("Series not found"));

            mockMvc.perform(get("/api/series/999/books"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/series tests")
    class CreateSeriesTests {

        @Test
        @DisplayName("Should create series successfully")
        void createSeries_ValidRequest_ReturnsCreatedSeries() throws Exception {
            when(seriesService.createSeries(any(SeriesRequest.class))).thenReturn(testSeriesResponse);

            mockMvc.perform(post("/api/series")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testSeriesRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Test Series"));

            verify(seriesService).createSeries(any(SeriesRequest.class));
        }

        @Test
        @DisplayName("Should create series with authorId for librarian")
        void createSeries_WithAuthorId_ReturnsCreatedSeries() throws Exception {
            testSeriesRequest.setAuthorId(1L);
            when(seriesService.createSeries(any(SeriesRequest.class))).thenReturn(testSeriesResponse);

            mockMvc.perform(post("/api/series")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testSeriesRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.author.id").value(1));

            verify(seriesService).createSeries(any(SeriesRequest.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/series/{id} tests")
    class UpdateSeriesTests {

        @Test
        @DisplayName("Should update series successfully")
        void updateSeries_ValidRequest_ReturnsUpdatedSeries() throws Exception {
            SeriesResponse updatedResponse = new SeriesResponse(
                    1L, "Updated Series", "Updated description",
                    new SeriesResponse.AuthorSummary(1L, "John", "Doe", null), 0, 0
            );
            testSeriesRequest.setTitle("Updated Series");
            testSeriesRequest.setDescription("Updated description");

            when(seriesService.updateSeries(eq(1L), any(SeriesRequest.class))).thenReturn(updatedResponse);

            mockMvc.perform(put("/api/series/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testSeriesRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Series"))
                    .andExpect(jsonPath("$.description").value("Updated description"));

            verify(seriesService).updateSeries(eq(1L), any(SeriesRequest.class));
        }

        @Test
        @DisplayName("Should return 404 when series not found")
        void updateSeries_SeriesNotFound_Returns404() throws Exception {
            when(seriesService.updateSeries(eq(999L), any(SeriesRequest.class)))
                    .thenThrow(new EntityNotFoundException("Series not found"));

            mockMvc.perform(put("/api/series/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testSeriesRequest)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/series/{id} tests")
    class DeleteSeriesTests {

        @Test
        @DisplayName("Should delete series successfully")
        void deleteSeries_SeriesExists_ReturnsNoContent() throws Exception {
            doNothing().when(seriesService).deleteSeries(1L);

            mockMvc.perform(delete("/api/series/1"))
                    .andExpect(status().isNoContent());

            verify(seriesService).deleteSeries(1L);
        }

        @Test
        @DisplayName("Should return 404 when series not found")
        void deleteSeries_SeriesNotFound_Returns404() throws Exception {
            doThrow(new EntityNotFoundException("Series not found"))
                    .when(seriesService).deleteSeries(999L);

            mockMvc.perform(delete("/api/series/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/series/{id}/books/{isbn} tests")
    class AddBookToSeriesTests {

        @Test
        @DisplayName("Should add book to series successfully")
        void addBookToSeries_ValidRequest_ReturnsUpdatedSeries() throws Exception {
            SeriesResponse responseWithBook = new SeriesResponse(
                    1L, "Test Series", "A test series description",
                    new SeriesResponse.AuthorSummary(1L, "John", "Doe", null), 1, 0
            );
            when(seriesService.addBookToSeries(1L, "9781234567890")).thenReturn(responseWithBook);

            mockMvc.perform(post("/api/series/1/books/9781234567890"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bookCount").value(1));

            verify(seriesService).addBookToSeries(1L, "9781234567890");
        }

        @Test
        @DisplayName("Should return 404 when series not found")
        void addBookToSeries_SeriesNotFound_Returns404() throws Exception {
            when(seriesService.addBookToSeries(999L, "9781234567890"))
                    .thenThrow(new EntityNotFoundException("Series not found"));

            mockMvc.perform(post("/api/series/999/books/9781234567890"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 when book not found")
        void addBookToSeries_BookNotFound_Returns404() throws Exception {
            when(seriesService.addBookToSeries(1L, "nonexistent"))
                    .thenThrow(new EntityNotFoundException("Book not found"));

            mockMvc.perform(post("/api/series/1/books/nonexistent"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/series/{id}/books/{isbn} tests")
    class RemoveBookFromSeriesTests {

        @Test
        @DisplayName("Should remove book from series successfully")
        void removeBookFromSeries_ValidRequest_ReturnsUpdatedSeries() throws Exception {
            when(seriesService.removeBookFromSeries(1L, "9781234567890")).thenReturn(testSeriesResponse);

            mockMvc.perform(delete("/api/series/1/books/9781234567890"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bookCount").value(0));

            verify(seriesService).removeBookFromSeries(1L, "9781234567890");
        }

        @Test
        @DisplayName("Should return 404 when series not found")
        void removeBookFromSeries_SeriesNotFound_Returns404() throws Exception {
            when(seriesService.removeBookFromSeries(999L, "9781234567890"))
                    .thenThrow(new EntityNotFoundException("Series not found"));

            mockMvc.perform(delete("/api/series/999/books/9781234567890"))
                    .andExpect(status().isNotFound());
        }
    }
}
