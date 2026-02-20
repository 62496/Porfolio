package com.prj2.booksta.repository;

import com.prj2.booksta.model.UserBookInventory;
import com.prj2.booksta.model.UserBookInventoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBookInventoryRepository extends JpaRepository<UserBookInventory, UserBookInventoryId> {
    List<UserBookInventory> findByUserId(Long userId);

    List<UserBookInventory> findByBookIsbn(String bookIsbn);

    List<UserBookInventory> findByBookIsbnAndQuantityGreaterThan(String bookIsbn, Long minQuantity);
}
