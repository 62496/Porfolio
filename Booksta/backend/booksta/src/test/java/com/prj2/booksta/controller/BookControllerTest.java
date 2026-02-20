package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj2.booksta.model.*;
import com.prj2.booksta.model.dto.CreateReadingEventRequest;
import com.prj2.booksta.model.dto.UpdateBook;
import com.prj2.booksta.repository.SeriesRepository;
import com.prj2.booksta.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

        @Mock
        private BookService bookService;

        @Mock
        private FileStorageService fileStorageService;

        @Mock
        private ImageService imageService;

        @Mock
        private ReportService reportService;

        @Mock
        private UserService userService;

        @Mock
        private SeriesRepository seriesRepository;

        @Mock
        private BookReadEventService bookReadEventService;

        @Mock
        private ReadingSessionService readingSessionService;

        @InjectMocks
        private BookController bookController;

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;
        private UserDetails mockUserDetails;
        private Authentication mockAuthentication;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper();

                mockUserDetails = mock(UserDetails.class);
                lenient().when(mockUserDetails.getUsername()).thenReturn("test@test.com");

                mockAuthentication = mock(Authentication.class);
                lenient().when(mockAuthentication.getName()).thenReturn("test@test.com");
                lenient().when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);

                HandlerMethodArgumentResolver putPrincipal = new HandlerMethodArgumentResolver() {
                        @Override
                        public boolean supportsParameter(MethodParameter parameter) {
                                return parameter.getParameterType().isAssignableFrom(UserDetails.class);
                        }

                        @Override
                        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                                return mockUserDetails;
                        }
                };

                mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                                .setMessageConverters(new MappingJackson2HttpMessageConverter(),
                                                new StringHttpMessageConverter())
                                .setCustomArgumentResolvers(putPrincipal)
                                .build();
        }

        @Test
        void testGetAllBooks() throws Exception {
                when(bookService.getAllBooks()).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/books")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());

                verify(bookService).getAllBooks();
        }

        @Test
        void testGetBookByIsbnFound() throws Exception {
                Book book = new Book();
                book.setIsbn("123");

                when(bookService.getBookByIsbn("123")).thenReturn(book);

                mockMvc.perform(get("/api/books/123")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.isbn").value("123"));
        }

        @Test
        void testSearchBooks() throws Exception {
                when(bookService.searchBooks("harry", null, null, null))
                                .thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/books/search")
                                .param("title", "harry")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void testGetBooksBySeries() throws Exception {
                when(bookService.findBySeriesId(10L)).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/books/series/10")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void testCreateBookWithImage() throws Exception {
                Book bookToSave = new Book();
                bookToSave.setIsbn("555");

                MockMultipartFile bookPart = new MockMultipartFile(
                                "book", "", "application/json",
                                objectMapper.writeValueAsBytes(bookToSave));

                MockMultipartFile imageFile = new MockMultipartFile(
                                "image", "cover.jpg", "image/jpeg", "content".getBytes());

                when(fileStorageService.saveBookImage(any(), eq("555"))).thenReturn("path.jpg");
                when(imageService.createImage(any(Image.class))).thenReturn(new Image());
                when(bookService.save(any(Book.class))).thenReturn(bookToSave);

                mockMvc.perform(multipart("/api/books")
                                .file(bookPart)
                                .file(imageFile))
                                .andExpect(status().isCreated());

                verify(fileStorageService).saveBookImage(any(), eq("555"));
        }

        @Test
        void testUpdateBook() throws Exception {
                UpdateBook updateDto = new UpdateBook(
                                2023,
                                300L,
                                "Updated Title",
                                "New Description",
                                null,
                                null,
                                null);

                MockMultipartFile bookPart = new MockMultipartFile(
                                "book", "", "application/json",
                                objectMapper.writeValueAsBytes(updateDto));

                Book updatedBook = new Book();
                updatedBook.setIsbn("123");
                updatedBook.setTitle("Updated Title");

                when(bookService.updateBook(any(UpdateBook.class), eq("123"), any()))
                                .thenReturn(updatedBook);

                mockMvc.perform(multipart("/api/books/123")
                                .file(bookPart)
                                .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                }))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("Updated Title"));

                verify(bookService).updateBook(any(UpdateBook.class), eq("123"), any());
        }

        @Test
        void testDeleteBook() throws Exception {
                doNothing().when(bookService).delete("123");

                mockMvc.perform(delete("/api/books/123"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").value("Deleted book: 123"));

                verify(bookService).delete("123");
        }

        @Test
        void testReportBook() throws Exception {
                String isbn = "12345";
                BookReport inputReport = new BookReport();

                Book book = new Book();
                book.setIsbn(isbn);
                User user = new User();
                user.setEmail("test@test.com");
                BookReport savedReport = new BookReport();
                savedReport.setId(1L);

                when(userService.getUserByEmail("test@test.com")).thenReturn(user);
                when(bookService.getBookByIsbn(isbn)).thenReturn(book);
                when(reportService.createBookReport(any(BookReport.class))).thenReturn(savedReport);

                mockMvc.perform(post("/api/books/{isbn}/reports", isbn)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputReport)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        void testCreateBookReadEvent() throws Exception {
                String isbn = "123";
                CreateReadingEventRequest request = new CreateReadingEventRequest(ReadingEventType.STARTED_READING);

                BookReadEvent event = new BookReadEvent();
                event.setId(100L);

                when(bookReadEventService.createReadEvent(eq("test@test.com"), eq(isbn), any()))
                                .thenReturn(event);

                mockMvc.perform(post("/api/books/{isbn}/read-events", isbn)
                                .principal(mockAuthentication)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(100));

                verify(bookReadEventService).createReadEvent(eq("test@test.com"), eq(isbn), any());
        }

        @Test
        void testGetLatestBookReadEvent() throws Exception {
                String isbn = "123";
                BookReadEvent event = new BookReadEvent();
                event.setId(200L);

                when(bookReadEventService.getLatestReadEvent("test@test.com", isbn))
                                .thenReturn(event);

                mockMvc.perform(get("/api/books/{isbn}/read-event/latest", isbn)
                                .principal(mockAuthentication))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(200));
        }

        @Test
        void testGetBookReadingSessions() throws Exception {
                String isbn = "123";
                User user = new User();
                user.setId(1L);

                when(userService.getUserByEmail("test@test.com")).thenReturn(user);
                when(readingSessionService.findByUserAndIsbn(user, isbn))
                                .thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/books/{isbn}/reading-sessions", isbn)
                                .principal(mockAuthentication))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());

                verify(userService).getUserByEmail("test@test.com");
                verify(readingSessionService).findByUserAndIsbn(user, isbn);
        }

        @Test
        void testGetBookReadEvents() throws Exception {
                String isbn = "123";
                User user = new User();
                user.setId(1L);

                when(userService.getUserByEmail("test@test.com")).thenReturn(user);
                when(bookReadEventService.findByUserAndIsbn(1L, isbn))
                                .thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/books/{isbn}/reading-events", isbn)
                                .principal(mockAuthentication))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());

                verify(bookReadEventService).findByUserAndIsbn(1L, isbn);
        }
}