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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final UserBookInventoryRepository inventoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    public InventoryService(UserBookInventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<InventoryResponse> getInventoryByUserId(Long userId) {
        return inventoryRepository.findByUserId(userId).stream()
                .map(InventoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<UserBookInventory> getInventoryItem(Long userId, String bookIsbn) {
        UserBookInventoryId id = new UserBookInventoryId(userId, bookIsbn);
        return inventoryRepository.findById(id);
    }

    @Transactional
    public InventoryResponse addToInventory(User user, InventoryRequest request) {
        Book book = bookRepository.findById(request.getBookIsbn())
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + request.getBookIsbn()));

        UserBookInventoryId id = new UserBookInventoryId(user.getId(), book.getIsbn());

        // Check if already exists
        Optional<UserBookInventory> existing = inventoryRepository.findById(id);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Book already in inventory. Use update endpoint to modify.");
        }

        UserBookInventory inventory = new UserBookInventory();
        inventory.setId(id);
        inventory.setUser(user);
        inventory.setBook(book);
        inventory.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1L);
        inventory.setPricePerUnit(request.getPricePerUnit());

        UserBookInventory saved = inventoryRepository.save(inventory);
        return InventoryResponse.fromEntity(saved);
    }

    @Transactional
    public InventoryResponse updateInventory(User user, String bookIsbn, InventoryRequest request) {
        UserBookInventoryId id = new UserBookInventoryId(user.getId(), bookIsbn);

        UserBookInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory item not found"));

        if (request.getQuantity() != null) {
            if (request.getQuantity() <= 0) {
                inventoryRepository.delete(inventory);
                throw new IllegalArgumentException("Quantity must be greater than 0. Item removed from inventory.");
            }
            inventory.setQuantity(request.getQuantity());
        }

        if (request.getPricePerUnit() != null) {
            inventory.setPricePerUnit(request.getPricePerUnit());
        }

        UserBookInventory saved = inventoryRepository.save(inventory);
        return InventoryResponse.fromEntity(saved);
    }

    @Transactional
    public void removeFromInventory(User user, String bookIsbn) {
        UserBookInventoryId id = new UserBookInventoryId(user.getId(), bookIsbn);

        if (!inventoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Inventory item not found");
        }

        inventoryRepository.deleteById(id);
    }
}
