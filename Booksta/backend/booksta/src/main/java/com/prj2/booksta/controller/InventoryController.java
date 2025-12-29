package com.prj2.booksta.controller;

import java.util.List;

import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.InventoryRequest;
import com.prj2.booksta.model.dto.InventoryResponse;
import com.prj2.booksta.service.InventoryService;
import com.prj2.booksta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;
    private final UserService userService;

    @Autowired
    public InventoryController(InventoryService inventoryService, UserService userService) {
        this.inventoryService = inventoryService;
        this.userService = userService;
    }

    /**
     * Get current user's inventory
     */
    @GetMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<InventoryResponse>> getMyInventory(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        List<InventoryResponse> inventory = inventoryService.getInventoryByUserId(currentUser.getId());
        return ResponseEntity.ok(inventory);
    }

    /**
     * Add a book to inventory
     */
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<InventoryResponse> addToInventory(
            @RequestBody InventoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        InventoryResponse response = inventoryService.addToInventory(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update inventory item (quantity and/or price)
     */
    @PutMapping("/{bookIsbn}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable String bookIsbn,
            @RequestBody InventoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        InventoryResponse response = inventoryService.updateInventory(currentUser, bookIsbn, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove a book from inventory
     */
    @DeleteMapping("/{bookIsbn}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> removeFromInventory(
            @PathVariable String bookIsbn,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        inventoryService.removeFromInventory(currentUser, bookIsbn);
        return ResponseEntity.noContent().build();
    }
}
