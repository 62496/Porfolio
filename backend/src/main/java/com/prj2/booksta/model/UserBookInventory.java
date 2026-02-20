package com.prj2.booksta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "user_book_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBookInventory {

    @EmbeddedId
    private UserBookInventoryId id = new UserBookInventoryId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("bookIsbn")
    @JoinColumn(name = "book_isbn")
    private Book book;

    @Column(nullable = false)
    private Long quantity = 1L;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerUnit = BigDecimal.ZERO;
}
