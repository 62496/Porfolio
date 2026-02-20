package com.prj2.booksta.controller;

import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.InventoryRequest;
import com.prj2.booksta.model.dto.InventoryResponse;
import com.prj2.booksta.service.InventoryService;
import com.prj2.booksta.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private InventoryController inventoryController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(99L);
        mockUser.setEmail("test@test.com");
    }

    @Test
    void getMyInventory_ShouldReturnList_WhenUserIsAuthenticated() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.getUserByEmail("test@test.com")).thenReturn(mockUser);
        when(inventoryService.getInventoryByUserId(99L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<InventoryResponse>> response = inventoryController.getMyInventory(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userService).getUserByEmail("test@test.com");
        verify(inventoryService).getInventoryByUserId(99L);
    }

    @Test
    void addToInventory_ShouldReturnCreated_WhenValid() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.getUserByEmail("test@test.com")).thenReturn(mockUser);

        InventoryRequest request = new InventoryRequest();
        request.setBookIsbn("ISBN-123");
        request.setQuantity(5L);
        request.setPricePerUnit(BigDecimal.valueOf(19.99));

        InventoryResponse mockResponse = new InventoryResponse();
        mockResponse.setUserId(99L);
        mockResponse.setBookIsbn("ISBN-123");
        mockResponse.setQuantity(5L);
        mockResponse.setPricePerUnit(BigDecimal.valueOf(19.99));

        when(inventoryService.addToInventory(eq(mockUser), any(InventoryRequest.class))).thenReturn(mockResponse);

        ResponseEntity<InventoryResponse> response = inventoryController.addToInventory(request, userDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ISBN-123", response.getBody().getBookIsbn());
        verify(inventoryService).addToInventory(eq(mockUser), any(InventoryRequest.class));
    }

    @Test
    void updateInventory_ShouldReturnOk_WhenValid() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.getUserByEmail("test@test.com")).thenReturn(mockUser);

        InventoryRequest request = new InventoryRequest();
        request.setQuantity(10L);
        request.setPricePerUnit(BigDecimal.valueOf(24.99));

        InventoryResponse mockResponse = new InventoryResponse();
        mockResponse.setUserId(99L);
        mockResponse.setBookIsbn("ISBN-123");
        mockResponse.setQuantity(10L);
        mockResponse.setPricePerUnit(BigDecimal.valueOf(24.99));

        when(inventoryService.updateInventory(eq(mockUser), eq("ISBN-123"), any(InventoryRequest.class))).thenReturn(mockResponse);

        ResponseEntity<InventoryResponse> response = inventoryController.updateInventory("ISBN-123", request, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getQuantity());
        verify(inventoryService).updateInventory(eq(mockUser), eq("ISBN-123"), any(InventoryRequest.class));
    }

    @Test
    void removeFromInventory_ShouldReturnNoContent_WhenValid() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.getUserByEmail("test@test.com")).thenReturn(mockUser);

        doNothing().when(inventoryService).removeFromInventory(mockUser, "ISBN-123");

        ResponseEntity<Void> response = inventoryController.removeFromInventory("ISBN-123", userDetails);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(inventoryService).removeFromInventory(mockUser, "ISBN-123");
    }
}
