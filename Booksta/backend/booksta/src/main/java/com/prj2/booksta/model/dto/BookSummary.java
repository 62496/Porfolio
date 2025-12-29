package com.prj2.booksta.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSummary {
    private String isbn;
    private String title;
    private Integer publishingYear;
    private String imageUrl;
}
