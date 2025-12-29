package com.prj2.booksta.service;

import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.User;
import com.prj2.booksta.model.UserBookInventory;
import com.prj2.booksta.model.UserBookInventoryId;
import com.prj2.booksta.model.dto.InventoryRequest;
import com.prj2.booksta.model.dto.InventoryResponse;
import com.prj2.booksta.repository.BookRepository;
import com.prj2.booksta.repository.UserBookInventoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private UserBookInventoryRepository inventoryRepository;

    @Mock
    private BookRepository bookRepository;

    private InventoryService inventoryService;

    private User mockUser;
    private Book mockBook;

    @BeforeEach
    void setUp() {
        // Create the service with constructor injection
        inventoryService = new InventoryService(inventoryRepository);
        // Manually inject the field-autowired bookRepository
        ReflectionTestUtils.setField(inventoryService, "bookRepository", bookRepository);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@test.com");

        mockBook = new Book();
        mockBook.setIsbn("123-ABC");
        mockBook.setTitle("Test Book");
    }

    @Test
    void getInventoryByUserId_ShouldReturnList() {
        Long userId = 1L;
        when(inventoryRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        List<InventoryResponse> result = inventoryService.getInventoryByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(inventoryRepository).findByUserId(userId);
    }

    @Test
    void addToInventory_ShouldSaveAndReturnResponse_WhenValid() {
        InventoryRequest request = new InventoryRequest();
        request.setBookIsbn("123-ABC");
        request.setQuantity(5L);
        request.setPricePerUnit(BigDecimal.valueOf(19.99));

        UserBookInventory savedInventory = new UserBookInventory();
        savedInventory.setId(new UserBookInventoryId(1L, "123-ABC"));
        savedInventory.setUser(mockUser);
        savedInventory.setBook(mockBook);
        savedInventory.setQuantity(5L);
        savedInventory.setPricePerUnit(BigDecimal.valueOf(19.99));

        when(bookRepository.findById("123-ABC")).thenReturn(Optional.of(mockBook));
        when(inventoryRepository.findById(any(UserBookInventoryId.class))).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(UserBookInventory.class))).thenReturn(savedInventory);

        InventoryResponse result = inventoryService.addToInventory(mockUser, request);

        assertNotNull(result);
        assertEquals("123-ABC", result.getBookIsbn());
        assertEquals(5L, result.getQuantity());
        assertEquals(BigDecimal.valueOf(19.99), result.getPricePerUnit());
        verify(inventoryRepository).save(any(UserBookInventory.class));
    }

    @Test
    void addToInventory_ShouldThrowException_WhenBookNotFound() {
        InventoryRequest request = new InventoryRequest();
        request.setBookIsbn("INVALID-ISBN");
        request.setQuantity(5L);
        request.setPricePerUnit(BigDecimal.valueOf(19.99));

        when(bookRepository.findById("INVALID-ISBN")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            inventoryService.addToInventory(mockUser, request)
        );
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void addToInventory_ShouldThrowException_WhenBookAlreadyInInventory() {
        InventoryRequest request = new InventoryRequest();
        request.setBookIsbn("123-ABC");
        request.setQuantity(5L);
        request.setPricePerUnit(BigDecimal.valueOf(19.99));

        UserBookInventory existingInventory = new UserBookInventory();
        existingInventory.setId(new UserBookInventoryId(1L, "123-ABC"));

        when(bookRepository.findById("123-ABC")).thenReturn(Optional.of(mockBook));
        when(inventoryRepository.findById(any(UserBookInventoryId.class))).thenReturn(Optional.of(existingInventory));

        assertThrows(IllegalArgumentException.class, () ->
            inventoryService.addToInventory(mockUser, request)
        );
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void updateInventory_ShouldUpdateAndReturnResponse_WhenValid() {
        InventoryRequest request = new InventoryRequest();
        request.setQuantity(10L);
        request.setPricePerUnit(BigDecimal.valueOf(24.99));

        UserBookInventory existingInventory = new UserBookInventory();
        existingInventory.setId(new UserBookInventoryId(1L, "123-ABC"));
        existingInventory.setUser(mockUser);
        existingInventory.setBook(mockBook);
        existingInventory.setQuantity(5L);
        existingInventory.setPricePerUnit(BigDecimal.valueOf(19.99));

        when(inventoryRepository.findById(any(UserBookInventoryId.class))).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(UserBookInventory.class))).thenReturn(existingInventory);

        InventoryResponse result = inventoryService.updateInventory(mockUser, "123-ABC", request);

        assertNotNull(result);
        assertEquals(10L, existingInventory.getQuantity());
        assertEquals(BigDecimal.valueOf(24.99), existingInventory.getPricePerUnit());
        verify(inventoryRepository).save(existingInventory);
    }

    @Test
    void updateInventory_ShouldThrowException_WhenInventoryItemNotFound() {
        InventoryRequest request = new InventoryRequest();
        request.setQuantity(10L);

        when(inventoryRepository.findById(any(UserBookInventoryId.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            inventoryService.updateInventory(mockUser, "123-ABC", request)
        );
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void removeFromInventory_ShouldDelete_WhenExists() {
        when(inventoryRepository.existsById(any(UserBookInventoryId.class))).thenReturn(true);

        inventoryService.removeFromInventory(mockUser, "123-ABC");

        verify(inventoryRepository).deleteById(any(UserBookInventoryId.class));
    }

    @Test
    void removeFromInventory_ShouldThrowException_WhenNotFound() {
        when(inventoryRepository.existsById(any(UserBookInventoryId.class))).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
            inventoryService.removeFromInventory(mockUser, "123-ABC")
        );
        verify(inventoryRepository, never()).deleteById(any());
    }
}
