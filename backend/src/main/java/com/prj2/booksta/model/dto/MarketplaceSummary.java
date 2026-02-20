package com.prj2.booksta.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceSummary {
    private BookInfo book;
    private BigDecimal lowestPrice;
    private int sellerCount;
    private long totalQuantityAvailable;
    private boolean inStock;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookInfo {
        private String isbn;
        private String title;
        private String description;
        private Integer publishingYear;
        private Long pages;
        private String imageUrl;
        private List<AuthorInfo> authors;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String imageUrl;
    }
}
