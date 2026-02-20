package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj2.booksta.model.BookCollection;
import com.prj2.booksta.service.BookCollectionService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookCollectionControllerTest {

    @Mock
    private BookCollectionService service;

    @InjectMocks
    private BookCollectionController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private BookCollection collection;
    private final Long COL_ID = 1L;
    private final String ISBN = "12345";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        collection = new BookCollection();
        collection.setId(COL_ID);
        collection.setName("My Favorites");
    }


    @Test
    void testCreate_WithImage() throws Exception {
        MockMultipartFile collectionPart = new MockMultipartFile(
                "collection",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(collection)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "cover.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        when(service.createCollection(any(BookCollection.class), any(MultipartFile.class)))
                .thenReturn(collection);

        mockMvc.perform(multipart("/api/collections")
                .file(collectionPart)
                .file(imagePart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(COL_ID));

        verify(service).createCollection(any(BookCollection.class), any(MultipartFile.class));
    }

    @Test
    void testUpdate_WithImage() throws Exception {
        MockMultipartFile collectionPart = new MockMultipartFile(
                "collection", "", "application/json", objectMapper.writeValueAsBytes(collection)
        );
        MockMultipartFile imagePart = new MockMultipartFile(
                "image", "new.jpg", "image/jpeg", "new content".getBytes()
        );

        when(service.updateCollection(eq(COL_ID), any(BookCollection.class), any(MultipartFile.class)))
                .thenReturn(collection);

        mockMvc.perform(multipart("/api/collections/{collectionId}", COL_ID)
                .file(collectionPart)
                .file(imagePart)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk());

        verify(service).updateCollection(eq(COL_ID), any(BookCollection.class), any(MultipartFile.class));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(service).deleteCollection(COL_ID);

        mockMvc.perform(delete("/api/collections/{collectionId}", COL_ID))
                .andExpect(status().isNoContent()); // 204

        verify(service).deleteCollection(COL_ID);
    }

    @Test
    void testGetOwnCollections() throws Exception {
        when(service.getAllOwnCollections()).thenReturn(Collections.singletonList(collection));

        mockMvc.perform(get("/api/collections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(COL_ID));
    }

    @Test
    void testGetAllowedCollections() throws Exception {
        when(service.getAllCollectionsAllowed()).thenReturn(Collections.singletonList(collection));

        mockMvc.perform(get("/api/collections/allowed"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPublicCollections() throws Exception {
        when(service.getAllPublicCollections()).thenReturn(Collections.singletonList(collection));

        mockMvc.perform(get("/api/collections/public"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetSharedWithMe() throws Exception {
        when(service.getSharedWithMe()).thenReturn(Collections.singletonList(collection));

        mockMvc.perform(get("/api/collections/shared"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCollection_Found() throws Exception {
        when(service.getCollectionIfAllowed(COL_ID)).thenReturn(Optional.of(collection));

        mockMvc.perform(get("/api/collections/{collectionId}", COL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(COL_ID));
    }

    @Test
    void testGetCollection_Forbidden() throws Exception {
        when(service.getCollectionIfAllowed(COL_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/collections/{collectionId}", COL_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCanAccess() throws Exception {
        when(service.canAccess(COL_ID)).thenReturn(true);

        mockMvc.perform(get("/api/collections/{collectionId}/access", COL_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testShareWith() throws Exception {
        String email = "friend@test.com";
        when(service.shareWith(COL_ID, email)).thenReturn(collection);

        mockMvc.perform(post("/api/collections/{collectionId}/share/{userEmail}", COL_ID, email))
                .andExpect(status().isOk());
    }

    @Test
    void testUnshareWith() throws Exception {
        Long userId = 99L;
        when(service.unshareWith(COL_ID, userId)).thenReturn(collection);

        mockMvc.perform(delete("/api/collections/{collectionId}/share/{userId}", COL_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void testAddBook() throws Exception {
        when(service.addBook(COL_ID, ISBN)).thenReturn(collection);

        mockMvc.perform(post("/api/collections/{collectionId}/books/{isbn}", COL_ID, ISBN))
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveBook() throws Exception {
        when(service.removeBook(COL_ID, ISBN)).thenReturn(collection);

        mockMvc.perform(delete("/api/collections/{collectionId}/books/{isbn}", COL_ID, ISBN))
                .andExpect(status().isOk());
    }

    @Test
    void testContainsBook() throws Exception {
        when(service.collectionContainsBook(COL_ID, ISBN)).thenReturn(true);

        mockMvc.perform(get("/api/collections/{collectionId}/books/{isbn}", COL_ID, ISBN))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}